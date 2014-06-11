package ca.ubc.cs.commandrecommender.generator;

/**
 * Created by KeEr on 2014-06-11.
 */
public abstract class AbstractGen {
    protected final EclipseCmdDevDB db;

    public AbstractGen(EclipseCmdDevDB db) {
        this.db = db;
    }

    /**
     * generate (amount) number of recommendations for user
     * @param user
     * @param amount
     */
    public abstract void updateRecommendationForUser(String user, int amount);

}
