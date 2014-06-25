package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.cf.LearningModel;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaOptions;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;

/**
 * This algorithm find similar users based on the usage history and discovery patterns
 * and make recommendations based on the information of these similar users
 *
 * Created by KeEr on 2014-06-23.
 */
public class UserBasedCFWithDiscoveryRecGen extends AbstractCFWithDiscoveryRecGen {

    private int neighborhoodSize;

    public UserBasedCFWithDiscoveryRecGen(String label, AbstractLearningAcceptance acceptance, MatejkaOptions ops, int neighborhoodSize){
        super(label, acceptance, ops);
        this.neighborhoodSize = neighborhoodSize;
    }

    @Override
    protected AbstractRecommender getRecommender(LearningModel model) {
        return new MatejkaUserBasedRecommender(neighborhoodSize, model, ops);
    }

}
