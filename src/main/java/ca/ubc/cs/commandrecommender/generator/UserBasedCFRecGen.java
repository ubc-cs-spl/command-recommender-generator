package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.cf.UDCUsageModel;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaOptions;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaUserBasedRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * This algorithm make recommendations by looking at the data of similar users determined by the
 * commands used
 *
 * Created by KeEr on 2014-06-23.
 */
public class UserBasedCFRecGen extends AbstractCFRecGen {

    private int neighborhoodSize;
    private MatejkaOptions ops;

    public UserBasedCFRecGen(String label, int neighborhoodSize, MatejkaOptions ops){
        super(label);
        this.neighborhoodSize = neighborhoodSize;
        this.ops = ops;
    }

    @Override
    protected Recommender getRecommender(UDCUsageModel m) {
        return new MatejkaUserBasedRecommender(neighborhoodSize,m,ops);
    }

}
