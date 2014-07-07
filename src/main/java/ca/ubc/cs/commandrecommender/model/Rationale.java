package ca.ubc.cs.commandrecommender.model;

import org.bson.BasicBSONObject;

/**
 * Rationale for a recommendations
 * //TODO this is mostly a stub.
 *
 * Created by KeEr on 2014-07-07.
 */
public class Rationale extends BasicBSONObject {

    public static final String USER_BASED_CF_INFO = "user based CF";
    public static final String LINTON_PERCENT_USAGE = "linton percent";
    public static final String LINTON_RANK = "linton rank";
    public static final String MOST_POP_LEARNING_PERCENT = "pop learning percent";
    public static final String DISCOVERY_PREREQ = "discovery prereq";
    public static final String HOOK_FOR = "hook for";
    double value;

    public Rationale() {
    }

    public Rationale(double value) {
        setValue(value);
    }

    /**
     * LintonTotal:             number of times a tool has been used
     * LintonUser:              number of users who used a tool
     * LearningRule:            number of times a tool has been learned from known commands
     * MostPopularLearningRule: number of times a tool has been learned
     * MostPrereqLearningRule:  number of prerequisite a user satisfy for learning a new command
     * ItemBasedCF/Discovery:   item based estimated preference for the command or the learning sequence
     * UserBasedCF/Discovery:   user based estimated preference for the command or the learning sequence
     *
     * @return
     */
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
