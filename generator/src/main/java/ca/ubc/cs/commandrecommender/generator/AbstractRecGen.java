package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.db.IRecommenderDB;
import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.User;

import java.util.List;

/**
 * Created by KeEr on 2014-06-11.
 */
public abstract class AbstractRecGen implements IRecGen {
    protected final IRecommenderDB db;
    protected final String algorithm;

    public AbstractRecGen(IRecommenderDB db, String algorithm) {
        this.db = db;
        this.algorithm = algorithm;
    }

    @Override
    public String getAlgorithmUsed() {
        return algorithm;
    }

    @Override
    public Iterable<Integer> getRecommendationsForUser(User user, int amount) {
        List<Integer> history = user.getToolUses().toolsUsedInOrder().asList();
        RecommendationCollector collector =
                new RecommendationCollector(user.getUserCode(), history, amount);
        fillRecommendations(collector);
        return collector;
    }

    /**
     * Determines what recommendation will be generated
     *
     * Subclass should use {@code rc} to generate recommendations which will then
     * be stored in {@code rc}
     *
     * @param rc is modified
     */
    protected abstract void fillRecommendations(RecommendationCollector rc);

}
