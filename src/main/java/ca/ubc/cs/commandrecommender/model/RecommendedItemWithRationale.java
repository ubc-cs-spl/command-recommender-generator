package ca.ubc.cs.commandrecommender.model;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

/**
 * The detailed rationale as to why a recommendation is made
 *
 * //TODO: this is more or less just a stub and needs to be remodeled
 */
public class RecommendedItemWithRationale implements RecommendedItem{

    private final RecommendedItem recommendedItem;
    private final Rationale reasonMap;

    public RecommendedItemWithRationale(RecommendedItem recommendedItem) {
        this.recommendedItem = recommendedItem;
        reasonMap = new Rationale();
    }

    @Override
    public long getItemID() {
        return recommendedItem.getItemID();
    }

    @Override
    public float getValue() {
        return recommendedItem.getValue();
    }

    public Rationale getRationale() {
        return reasonMap;
    }

    public void put(String key, Object val) {
        reasonMap.put(key, val);
    }

    public RecommendedItemWithRationale append(String key, Object val) {
        put(key, val);
        return this;
    }

}
