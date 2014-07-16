package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.Rationale;
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

    public abstract void saveRecommendation(String commandId,
                                            String userId,
                                            String reason,
                                            String algorithmType,
                                            Rationale rationale);
    

    public abstract int getNumberOfKnownCommands(); 

    public abstract List<User> getAllUsers();

    public abstract void updateRecommendationStatus(String userId, String algoType);
    
    public abstract void clearInfoAndRankings(String userId, String algoType);
}
