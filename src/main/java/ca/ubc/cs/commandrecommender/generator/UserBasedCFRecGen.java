package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.cf.ReasonedRecommender;
import ca.ubc.cs.commandrecommender.model.cf.UDCUsageModel;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaOptions;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaUserBasedRecommender;

/**
 * This algorithm make recommendations by looking at the data of similar users determined by the
 * commands used
 */
public class UserBasedCFRecGen extends AbstractCFRecGen {

    private int neighborhoodSize;
    private MatejkaOptions ops;

    public UserBasedCFRecGen(String label, int neighborhoodSize, MatejkaOptions ops, int numOfCmd){
        super(label, numOfCmd);
        this.neighborhoodSize = neighborhoodSize;
        this.ops = ops;
    }

    @Override
    protected ReasonedRecommender getRecommender(UDCUsageModel m) {
        return new MatejkaUserBasedRecommender(neighborhoodSize,m,ops);
    }

}
