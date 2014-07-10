package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.User;

import java.util.List;

/**
 * Created by Spencer on 6/23/2014.
 */
public abstract class AbstractRecommendationDB {
    protected IndexMap userIndexMap;


    public AbstractRecommendationDB(IndexMap userIndexMap){
        this.userIndexMap = userIndexMap;
    }

    public abstract void saveRecommendation(String commandId, String userId, String reason, double reasonValue, String algorithmType, double algorithmValue);
    public abstract void markRecommendationsAsOld(String userId);
    public abstract List<User> getAllUsers();
}
