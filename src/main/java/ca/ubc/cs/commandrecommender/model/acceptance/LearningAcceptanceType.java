package ca.ubc.cs.commandrecommender.model.acceptance;

/**
 * All acceptance levels for algorithms involving temporal infomation ("learning").
 * See {@link ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance} for
 * more details.
 *
 * Created by KeEr on 2014-06-23.
 */
public enum  LearningAcceptanceType {

    /**
     * See {@link ca.ubc.cs.commandrecommender.model.acceptance.IncludeAllAcceptance}
     */
    INCLUDE_ALL,
    /**
     * See {@link ca.ubc.cs.commandrecommender.model.acceptance.MultiSessionAcceptance}
     */
    MULTI_SESSION,
    /**
     * See {@link ca.ubc.cs.commandrecommender.model.acceptance.MultiUseAcceptance}
     */
    MULTI_USE;

    /**
     * Retrieve the {@link ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance}
     * corresponding to the LearningAcceptanceType
     * @return
     */
    public AbstractLearningAcceptance getAcceptance() {
        switch (this) {
            case INCLUDE_ALL:
                return new IncludeAllAcceptance();
            case MULTI_SESSION:
                return new MultiSessionAcceptance();
            case MULTI_USE:
                return new MultiUseAcceptance();
            default:
                return new IncludeAllAcceptance();
        }
    }
}
