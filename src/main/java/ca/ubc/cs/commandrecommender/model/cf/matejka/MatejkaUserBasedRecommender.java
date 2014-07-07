package ca.ubc.cs.commandrecommender.model.cf.matejka;

import ca.ubc.cs.commandrecommender.model.Rationale;
import ca.ubc.cs.commandrecommender.model.RecommendedItemWithRationale;
import ca.ubc.cs.commandrecommender.model.cf.ReasonedRecommender;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User-based recommender based on Matejka tuning and similarity.
 * Mostly copied from {@link org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender}
 */
public final class MatejkaUserBasedRecommender extends AbstractRecommender implements UserBasedRecommender, ReasonedRecommender{

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
        return recommend(userID, howMany, rescorer, checkArgumentsAndGetNeighborhood(userID, howMany));
    }

    @Override
    public List<RecommendedItemWithRationale> recommendWithRationale(long userId, int howMany)
            throws TasteException {
        long[] theNeighborhood = checkArgumentsAndGetNeighborhood(userId, howMany);
        List<RecommendedItemWithRationale> itemsWithRationale = new ArrayList<RecommendedItemWithRationale>();
        for (RecommendedItem item : recommend(userId, howMany, null, theNeighborhood)) {
            RecommendedItemWithRationale itemWithRationale = new RecommendedItemWithRationale(item);
            double proportion = getProportionOfSimilarUsersWhoUsedTheItem(userId, theNeighborhood, item.getItemID());
            UserBasedCFInfo info = new UserBasedCFInfo(proportion, theNeighborhood.length);
            itemWithRationale.put(Rationale.USER_BASED_CF_INFO, info);
            itemsWithRationale.add(itemWithRationale);
        }
        return itemsWithRationale;
    }

    private long[] checkArgumentsAndGetNeighborhood(long userID, int howMany) throws TasteException {
        if (userID < 0) {
            throw new IllegalArgumentException("userID is negative");
        }
        if (howMany < 1) {
            throw new IllegalArgumentException("howMany must be at least 1");
        }
        return neighborhood.getUserNeighborhood(userID);
    }

    private List<RecommendedItem> recommend(long userID, int howMany,
                                           IDRescorer rescorer, long[] theNeighborhood) throws TasteException {
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

    // basically, the estimated preference is the total of preference value of each user
    // in the neighborhood for the item of interest, weighted by the similarity between the two users
    private double doEstimatePreference(long userid, long[] theNeighborhood, long itemid)
            throws TasteException {
        if (theNeighborhood.length==0) {
            return Double.NaN;
        }
        double preference = 0.0;
        //double totalSimilarity = 0.0; //from GenericUserBasedRecommender
        DataModel model = getDataModel();
        for (long uid : theNeighborhood) {
            if (uid!=userid) {
                // See GenericItemBasedRecommender.doEstimatePreference() too
                Float preferenceValue = model.getPreferenceValue(uid, itemid);
                if (preferenceValue != null) {
                    double theSimilarity = similarity.userSimilarity(userid, uid);
                    //+ 1.0; //this was given in the original algo, but not in matejka
                    if (!Double.isNaN(theSimilarity)) {
                        preference += w(theSimilarity) * alpha * preferenceValue;
                        //totalSimilarity += theSimilarity; //from GenericUserBasedRecommender
                    }
                }
            }
        }
        return preference == 0.0 ? Double.NaN : preference;
    }

    /**
     * //TODO: this method should be called in some version of recommend() that returns a more detailed recommended item
     * @param userId
     * @param theNeighborhood
     * @param itemId
     * @return the related information for the recommendation
     * @throws TasteException
     */
    private Double getProportionOfSimilarUsersWhoUsedTheItem(long userId, long[] theNeighborhood, long itemId)
            throws TasteException{
        int numUsersWhoUsedThis = 0;
        DataModel model = getDataModel();
        for (long uid : theNeighborhood) {
            Float preferenceValue = model.getPreferenceValue(uid, itemId);
            if (uid!=userId && (preferenceValue != null)) {
                numUsersWhoUsedThis++;
            }
        }
        return numUsersWhoUsedThis / (double) theNeighborhood.length;
    }

    //TODO: Matejka's weighting function (p. 196); undefined in paper
    //      At least we should know it even if we can't do anything about it
    private double w(double similarityBetweenUsers) {
        return similarityBetweenUsers;
    }

    //get the set of items that the user do not have preference for,
    //but some of the user's neighbors do
    private FastIDSet getAllOtherItems(long[] theNeighborhood, long userId) {
        DataModel model = getDataModel();
        FastIDSet allItems = new FastIDSet();
        try {
            for (long uid : theNeighborhood) {
                PreferenceArray preferencesFromUser = model.getPreferencesFromUser(uid);
                long[] ids = preferencesFromUser.getIDs();

                for (long itemId : ids) {
                    // If not already preferred by the user, add it
                    if (model.getPreferenceValue(userId, itemId) == null) {
                        //TODO: hopefully this code works fine
                        allItems.add(itemId);
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

    //A wrapper classs for estimating the similarity between a user and another one
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

    // A wrapper class for estimating the preference of a user for an item
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