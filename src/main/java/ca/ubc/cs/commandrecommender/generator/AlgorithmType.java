package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaOptions;
import ca.ubc.cs.commandrecommender.model.cf.matejka.Parameters;

/**
 * All algorithms available to generate recommendations from usage data.
 * See {@link ca.ubc.cs.commandrecommender.generator.IRecGen} and
 * {@link ca.ubc.cs.commandrecommender.generator.AbstractRecGen} for more details.
 *
 * Created by KeEr on 2014-06-19.
 */
public enum AlgorithmType {

    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.LintonTotalRecGen}
     */
    MOST_FREQUENTLY_USED("Percentage of all command usages"),
    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.LintonUserRecGen}
     */
    MOST_WIDELY_USED("Percentage of users that use this command"),
    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.HotkeyRecGen}
     */
    HOTKEY_NOT_USED("You have not used this command with a hotkey"),
    //TODO: make the reason provided more understandable
    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.LearningRuleRecGen}
     */
    LEARNING_RULE("advanced discovery"),
    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.MostPopularLearningRuleRecGen}
     */
    MOST_POPULAR_LEARNING_RULE("most popular discovery"),
    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.ItemBasedCFWithDiscoveryRecGen}
     */
    ITEM_BASED_CF_WITH_DISCOVERY("item based CF with discovery"),
    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.UserBasedCFWithDiscoveryRecGen}
     */
    USER_BASED_CF_WITH_DISCOVERY("user based CF with discovery"),
    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.ItemBasedCFRecGen}
     */
    ITEM_BASED_CF("item based CF"),
    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.UserBasedCFRecGen}
     */
    USER_BASED_CF("user based CF"),
    /**
     * See {@link ca.ubc.cs.commandrecommender.generator.LatentModelBasedCFRecGen}
     */
    LATENT_MODEL_BASED_CF("user and item based CF");

    private String reason;

    AlgorithmType(String reason) {
        this.reason = reason;
    }

    /**
     * Get a rational for how the algorithm generated the recommendation
     * @return
     */
    public String getRationale() {
        return reason;
    }

    /**
     * Retrieve the {@link ca.ubc.cs.commandrecommender.generator.IRecGen} corresponding
     * to the AlgorithmType and provided parameters
     * @param acceptance
     * @return
     */
    public IRecGen getRecGen(AbstractLearningAcceptance acceptance) {
        switch (this) {
            case MOST_FREQUENTLY_USED:
                return new LintonTotalRecGen(reason);
            case MOST_WIDELY_USED:
                return new LintonUserRecGen(reason);
            case HOTKEY_NOT_USED:
                return new HotkeyRecGen(reason);
            case LEARNING_RULE:
                return new LearningRuleRecGen(reason, acceptance);
            case MOST_POPULAR_LEARNING_RULE:
                return new MostPopularLearningRuleRecGen(reason, acceptance);
            case ITEM_BASED_CF_WITH_DISCOVERY:
                return new ItemBasedCFWithDiscoveryRecGen(reason, acceptance,
                        new MatejkaOptions(false, true, 1.0));
            case USER_BASED_CF_WITH_DISCOVERY:
                return new UserBasedCFWithDiscoveryRecGen(reason, acceptance,
                        new MatejkaOptions(false, true, 1.0), 32);
            case ITEM_BASED_CF:
                return new ItemBasedCFRecGen(reason,
                        new MatejkaOptions(false, true, Parameters.alpha));
            case USER_BASED_CF:
                return new UserBasedCFRecGen(reason,32,
                        new MatejkaOptions(false, true, 1.0));
            case LATENT_MODEL_BASED_CF:
                return new LatentModelBasedCFRecGen(reason, Parameters.numFeatures,
                        Parameters.lambda, Parameters.numIterations, Parameters.alpha);
            default: //We should never reach here unless we forget to update this method
                return new LintonTotalRecGen(reason);
        }
    }

    /**
     * @return whether the algorithm type requires a learning acceptance to be
     * properly constructed
     */
    public boolean needsAcceptance() {
        switch (this) {
            case LEARNING_RULE:
            case MOST_POPULAR_LEARNING_RULE:
            case ITEM_BASED_CF_WITH_DISCOVERY:
            case USER_BASED_CF_WITH_DISCOVERY:
                return true;
            default:
                return false;
        }
    }
}
