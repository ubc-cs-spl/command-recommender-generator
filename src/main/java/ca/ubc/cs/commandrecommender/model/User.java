package ca.ubc.cs.commandrecommender.model;

import java.util.Collection;
import java.util.Set;

/**
 * Created by KeEr on 2014-06-19.
 */
public class User {

    public int getUserCode() {
        return -1; //TODO
    }

    public String getUserId() {
        return null; //TODO
    }

    public ToolUseCollection getToolUses() {
        return null; //TODO
    }

    public boolean isTimeToGenerateRecs() {
        return true; //TODO
    }

    public Set<Integer> retrieveRecommendations() {
        return null; //TODO
    }

    public void saveRecommendations(Iterable<Integer> recs, String reason) {
        //TODO: not sure what param Type is the best; maybe we should just insert a single one
        //      and leave the looping to the Updater?
    }

    public void markAllRecommendationOld() {
        //TODO
    }

    public void updateRecommendationStatus() {
        //TODO
    }
}
