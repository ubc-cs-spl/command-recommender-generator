package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.cf.UDCUsageModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * This algorithm is like a combination of user-based and item-based collaborative filtering
 *
 * Created by KeEr on 2014-06-23.
 */
public class LatentModelBasedCFRecGen extends AbstractCFRecGen {

    private double alpha;
    private int numIterations;
    private int lambda;
    private int numFeatures;

    public LatentModelBasedCFRecGen(String label, int numFeatures, int lambda,
                                       int numIterations, double alpha) {
        super(label);
        this.numFeatures = numFeatures;
        this.lambda = lambda;
        this.numIterations = numIterations;
        this.alpha = alpha;
    }

    @Override
    protected Recommender getRecommender(UDCUsageModel m) throws TasteException {
        return new SVDRecommender(m,
                new ALSWRFactorizer(m, numFeatures, lambda, numIterations, true, alpha));
    }

}

