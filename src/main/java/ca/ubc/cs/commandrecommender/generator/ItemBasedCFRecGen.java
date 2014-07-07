package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.cf.ReasonedRecommender;
import ca.ubc.cs.commandrecommender.model.cf.UDCUsageModel;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaItemBasedRecommender;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaOptions;

/**
 * This algorithm determines recommendation through collaborative filtering
 * based on the commands used
 *
 * Created by KeEr on 2014-06-23.
 */
public class ItemBasedCFRecGen extends AbstractCFRecGen {

    private MatejkaOptions ops;

    public ItemBasedCFRecGen(String label, MatejkaOptions ops) {
        super(label);
        this.ops = ops;
    }

    protected ReasonedRecommender getRecommender(UDCUsageModel m) {
        return new MatejkaItemBasedRecommender(m,ops);
    }

}
