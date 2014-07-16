package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.cf.ReasonedRecommender;
import ca.ubc.cs.commandrecommender.model.cf.ReasonedSVDRecommender;
import ca.ubc.cs.commandrecommender.model.cf.UDCUsageModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;

/**
 * This algorithm is like a combination of user-based and item-based collaborative filtering.
 * Underneath, it uses SVD (singular value decomposition) with matrix factorization.
 */
public class LatentModelBasedCFRecGen extends AbstractCFRecGen {

    private double alpha;
    private int numIterations;
    private int lambda;
    private int numFeatures;

    public LatentModelBasedCFRecGen(String label, int numFeatures, int lambda,
                                       int numIterations, double alpha, int numOfCmd) {
        super(label, numOfCmd);
        this.numFeatures = numFeatures;
        this.lambda = lambda;
        this.numIterations = numIterations;
        this.alpha = alpha;
    }

    @Override
    protected ReasonedRecommender getRecommender(UDCUsageModel m) throws TasteException {
        return new ReasonedSVDRecommender(m,
                new ALSWRFactorizer(m, numFeatures, lambda, numIterations, true, alpha)) {
        };
    }

}

