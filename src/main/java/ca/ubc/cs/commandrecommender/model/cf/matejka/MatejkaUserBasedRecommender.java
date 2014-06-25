package ca.ubc.cs.commandrecommender.model.cf.matejka;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveArrayIterator;
import org.apache.mahout.cf.taste.impl.common.RefreshHelper;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.TopItems;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.LongPair;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Mostly copied from {@link org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender}
 */
//TODO: check over
public final class MatejkaUserBasedRecommender extends AbstractRecommender implements UserBasedRecommender {

  private UserNeighborhood neighborhood;
  private UserSimilarity similarity;
  private final RefreshHelper refreshHelper;
  
  /*
   * Alpha tuning param from matejka p. 196
   */
  private double alpha;

  public MatejkaUserBasedRecommender(int neighborhoodSize, DataModel dataModel, MatejkaOptions ops) {
    super(dataModel);
    
	try {
		similarity = new MatejkaSimilarity(dataModel, ops);
		neighborhood = new NearestNUserNeighborhood(neighborhoodSize, similarity, dataModel);
	} catch (TasteException e) {
		e.printStackTrace();
	}
    refreshHelper = new RefreshHelper(null);
    refreshHelper.addDependency(dataModel);
    refreshHelper.addDependency(similarity);
    refreshHelper.addDependency(neighborhood);
    
    this.alpha = ops.alpha;
  }

  @Override
  public List<RecommendedItem> recommend(long userID, int howMany,
  		IDRescorer rescorer) throws TasteException {
    if (userID < 0) {
      throw new IllegalArgumentException("userID is negative");
    }
    if (howMany < 1) {
      throw new IllegalArgumentException("howMany must be at least 1");
    }

//    User theUser = getDataModel().getUser(userID);
    long[] theNeighborhood = neighborhood.getUserNeighborhood(userID);
    
    if (theNeighborhood.length==0) {
      return Collections.emptyList();
    }

    FastIDSet allItems = getAllOtherItems(theNeighborhood, userID);

    TopItems.Estimator<Long> estimator = new Estimator(userID, theNeighborhood);

    List<RecommendedItem> topItems = TopItems.getTopItems(howMany, new LongPrimitiveArrayIterator(allItems.toArray()), rescorer, estimator);

    return topItems;
  }

  @Override
  public float estimatePreference(long userID, long itemID) throws TasteException {
    DataModel model = getDataModel();
    Float actualPref = model.getPreferenceValue(userID, itemID);
    if (actualPref != null) {
      return (float) (alpha*actualPref);
    }
    long[] theNeighborhood = neighborhood.getUserNeighborhood(userID);
    return (float) doEstimatePreference(userID, theNeighborhood, itemID);
  }

  @Override
  public long[] mostSimilarUserIDs(long userID, int howMany)
  		throws TasteException {
    return mostSimilarUserIDs(userID, howMany, null);
  }

  @Override
  public long[] mostSimilarUserIDs(long userID, int howMany,
  		Rescorer<LongPair> rescorer) throws TasteException {
    TopItems.Estimator<Long> estimator = new MostSimilarEstimator(userID, similarity, rescorer);
    return doMostSimilarUsers(howMany, estimator);
  }

  private long[] doMostSimilarUsers(int howMany,
                                        TopItems.Estimator<Long> estimator) throws TasteException {
    DataModel model = getDataModel();
    return TopItems.getTopUsers(howMany, model.getUserIDs(), null, estimator);
  }

  private double doEstimatePreference(long userid, long[] theNeighborhood, long itemid)
          throws TasteException {
    if (theNeighborhood.length==0) {
      return Double.NaN;
    }
    double preference = 0.0;
    //double totalSimilarity = 0.0;
    for (long uid : theNeighborhood) {
      if (uid!=userid) {
    	  DataModel model = getDataModel();
        // See GenericItemBasedRecommender.doEstimatePreference() too
    	Float preferenceValue = model.getPreferenceValue(uid, itemid);
        if (preferenceValue != null) {
          double theSimilarity = similarity.userSimilarity(userid, uid);
          	//+ 1.0; //this was given in the original algo, but not in matejka
          if (!Double.isNaN(theSimilarity)) {
            preference += w(theSimilarity) * alpha * preferenceValue;
            //totalSimilarity += theSimilarity;
          }
        }
      }
    }
    return preference == 0.0 ? Double.NaN : preference;
  }

  //Matejka's weighting function (p. 196); undefined in paper
  private double w(double similarityBetweenUsers) {
	return similarityBetweenUsers;
  }

	private FastIDSet getAllOtherItems(long[] theNeighborhood, long userid) {
	  
		DataModel model = getDataModel();
		FastIDSet allItems = new FastIDSet();
		try {
			for (long uid : theNeighborhood) {
				PreferenceArray preferencesFromUser = model.getPreferencesFromUser(uid);
				long[] iDs = preferencesFromUser.getIDs();

				for (int i = 0; i < iDs.length; i++) {
					long itemid= iDs[i];
					// If not already preferred by the user, add it
					if (model.getPreferenceValue(userid, itemid) == null) { //TODO: hopefully this code works fine
						allItems.add(itemid);
					}
				}
			}
		} catch (TasteException e) {
			e.printStackTrace();
		}
		return allItems;
  }

  @Override
  public void refresh(Collection<Refreshable> alreadyRefreshed) {
    refreshHelper.refresh(alreadyRefreshed);
  }

  @Override
  public String toString() {
    return "MatejkaUserBasedRecommender[neighborhood:" + neighborhood + ']';
  }

  private static class MostSimilarEstimator implements TopItems.Estimator<Long> {

    private final Long toUser;
    private final UserSimilarity similarity;
    private final Rescorer<LongPair> rescorer;

    private MostSimilarEstimator(Long toUserid,
                                 UserSimilarity similarity,
                                 Rescorer<LongPair> rescorer) {
      this.toUser = toUserid;
      this.similarity = similarity;
      this.rescorer = rescorer;
    }

    @Override
    public double estimate(Long userid) throws TasteException {
      // Don't consider the user itself as a possible most similar user
      if (userid.equals(toUser)) {
        return Double.NaN;
      }
      LongPair longPair = new LongPair(toUser,userid);
      if (rescorer != null && rescorer.isFiltered(longPair)) {
        return Double.NaN;
      }
      double originalEstimate = similarity.userSimilarity(toUser, userid);
      return rescorer == null ? originalEstimate : rescorer.rescore(longPair, originalEstimate);
    }
  }

  private final class Estimator implements TopItems.Estimator<Long> {

    private final long theUser;
    private final long[] theNeighborhood;

    Estimator(long theUser, long[] theNeighborhood) {
      this.theUser = theUser;
      this.theNeighborhood = theNeighborhood;
    }

    @Override
    public double estimate(Long item) throws TasteException {
      return doEstimatePreference(theUser, theNeighborhood, item);
    }
  }

}