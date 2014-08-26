package ca.ubc.cs.commandrecommender.generator;


import ca.ubc.cs.commandrecommender.model.RecommendationCollector;

/**
 * The base class for all classes implementing
 * {@link ca.ubc.cs.commandrecommender.generator.IRecGen}
 */
public abstract class AbstractRecGen implements IRecGen {
	
    protected final String algorithm;

    public AbstractRecGen(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String getAlgorithmUsed() {
        return algorithm;
    }

    @Override
    public void getRecommendationsForUser(RecommendationCollector collector) {
        fillRecommendations(collector);
    }

    /**
     * Determines what recommendation will be generated
     *
     * Subclass should use {@code rc} to generate recommendations which will then
     * be stored in {@code rc}
     *
     * @param rc is modified
     */
    //TODO: we are currently filtering out used commands before putting a candidate recommendation
    //      into the collector; we may or may not want to change this behaviour
    protected abstract void fillRecommendations(RecommendationCollector rc);

}
