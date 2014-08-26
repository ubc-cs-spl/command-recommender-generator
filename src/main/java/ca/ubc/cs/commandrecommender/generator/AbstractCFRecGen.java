package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.Rationale;
import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.RecommendedItemWithRationale;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import ca.ubc.cs.commandrecommender.model.cf.ReasonedRecommender;
import ca.ubc.cs.commandrecommender.model.cf.UDCUsageModel;
import org.apache.commons.collections4.Bag;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

import java.util.List;

/**
 * The base class for collaborative filtering algorithms based solely on
 * the command used (ie. do not take into account of the order) to generate
 * recommendations
 */
public abstract class AbstractCFRecGen extends AbstractRecGen {

    private UDCUsageModel model = new UDCUsageModel();
    private ReasonedRecommender recommender;
    private final int numberOfCommands;

    public AbstractCFRecGen(String label, int numberOfCommands) {
        super(label);
        this.numberOfCommands = numberOfCommands;
    }

    /**
     * build a model from the usage history
     * @param uses
     */
    @Override
    public void trainWith(ToolUseCollection uses) {
        Bag<Integer> bag = uses.toolsUsedBag();
        for(Integer toolId : bag.uniqueSet()){
            model.insertUse(bag.getCount(toolId), uses.userId, toolId);
        }
    }

    /**
     * Obtain recommender based on the model
     */
    @Override
    public void runAlgorithm(){
        model.finish();
        try {
            recommender = getRecommender(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fill in the {@code rc} with the recommendations made by the {@code recommender}
     * @param rc is modified
     */
    @Override
    public  void fillRecommendations(RecommendationCollector rc) {
        try {
            List<RecommendedItemWithRationale> items = recommender.recommendWithRationale(rc.userId, numberOfCommands);
            for(RecommendedItemWithRationale item : items){
                Rationale rationale = item.getRationale();
                rc.add((int)item.getItemID(), rationale);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Subclass implementation determines what kind of collaborative filtering
     * recommender will be used
     * @param m
     * @return
     * @throws TasteException
     */
    protected abstract ReasonedRecommender getRecommender(UDCUsageModel m) throws TasteException;

    DataModel getModel() {
        return model;
    }

}
