package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.db.IRecommenderDB;

import java.util.List;

/**
 * Created by KeEr on 2014-06-11.
 */
public abstract class AbstractGen {
    protected final IRecommenderDB db;
    protected final String algorithm;

    public AbstractGen(IRecommenderDB db, String algorithm) {
        this.db = db;
        this.algorithm = algorithm;
    }

    /**
     *
     * @return the algorithm used to determine the recommendation
     */
    public String getAlgorithmUsed() {
        return algorithm;
    }

    /**
     * generate (amount) number of recommendations for user
     * @param user
     * @param amount
     */
    public abstract List<String> getRecommendationsForUser(String user, int amount);

}
