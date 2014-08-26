package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.cf.LearningModel;
import ca.ubc.cs.commandrecommender.model.cf.ReasonedRecommender;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaItemBasedRecommender;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaOptions;

/**
 * This algorithm determines recommendation by using similar discovery (usage) patterns
 */
public class ItemBasedCFWithDiscoveryRecGen extends AbstractCFWithDiscoveryRecGen {

    public ItemBasedCFWithDiscoveryRecGen(String label,
                                          AbstractLearningAcceptance acceptance, MatejkaOptions ops) {
        super(label, acceptance, ops);
    }

    protected ReasonedRecommender getRecommender(LearningModel model) {
        return new MatejkaItemBasedRecommender(model,ops);
    }

}
