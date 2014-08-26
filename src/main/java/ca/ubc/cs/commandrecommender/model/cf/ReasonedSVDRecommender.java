package ca.ubc.cs.commandrecommender.model.cf;

import ca.ubc.cs.commandrecommender.model.RecommendedItemWithRationale;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wrapper class for SVDRecommender to help us extract values and reasons from algorithms
 * Created by KeEr on 2014-07-07.
 */
public class ReasonedSVDRecommender implements ReasonedRecommender {

    private SVDRecommender recommender;

    public ReasonedSVDRecommender(DataModel dataModel, Factorizer factorizer) throws TasteException {
        recommender = new SVDRecommender(dataModel, factorizer);
    }

    @Override
    public List<RecommendedItemWithRationale> recommendWithRationale(long userId, int howMany) throws TasteException {
        List<RecommendedItemWithRationale> itemsWithRationale = new ArrayList<RecommendedItemWithRationale>();
        for (RecommendedItem item : recommend(userId, howMany)) {
            itemsWithRationale.add(new RecommendedItemWithRationale(item));
        }
        return itemsWithRationale;
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException {
        return recommender.recommend(userID, howMany);
    }

    @Override
    public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
        return recommender.recommend(userID, howMany, rescorer);
    }

    @Override
    public float estimatePreference(long userID, long itemID) throws TasteException {
        return 0;
    }

    @Override
    public void setPreference(long userID, long itemID, float value) throws TasteException {
        recommender.setPreference(userID, itemID, value);
    }

    @Override
    public void removePreference(long userID, long itemID) throws TasteException {
        recommender.removePreference(userID, itemID);
    }

    @Override
    public DataModel getDataModel() {
        return recommender.getDataModel();
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        recommender.refresh(alreadyRefreshed);
    }
}
