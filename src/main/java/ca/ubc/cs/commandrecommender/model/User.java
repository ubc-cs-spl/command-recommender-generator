package ca.ubc.cs.commandrecommender.model;

import ca.ubc.cs.commandrecommender.db.AbstractRecommendationDB;
import java.sql.Date;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by KeEr on 2014-06-19.
 */
public class User {
    private String userId;
    private Date lastUpdate;
    private ToolUseCollection pastRecommendations;
    private AbstractRecommendationDB recommendationDB;
    private int WINDOW_IN_DAYS = 5;

    public User(String userId, Date lastUpdate, ToolUseCollection toolUses, AbstractRecommendationDB recommendationDB){
        this.userId = userId;
        this.lastUpdate = lastUpdate;
        this.pastRecommendations = toolUses;
        this.recommendationDB = recommendationDB;
    }

    public String getUserId() {
        return userId;
    }

    public ToolUseCollection getPastRecommendations() {
        return pastRecommendations;
    }

    public boolean isTimeToGenerateRecs() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -WINDOW_IN_DAYS);
        Date beginningOfWindow = new Date(now.getTimeInMillis());
        if(lastUpdate.before(beginningOfWindow))
            return false;
        else
            return true;
    }

    public Set<Integer> retrieveRecommendations() {
        return null; //TODO
    }

    public void saveRecommendations(Iterable<Integer> recommendations, String reason, IndexMap toolIndexMap) {
        recommendationDB.markRecommendationsAsOld(userId);
        for(Integer recommendation : recommendations){
            String commandId = toolIndexMap.getItemByIndex(recommendation);
            recommendationDB.saveRecommendation(commandId, userId, reason);
        }
    }


    public void updateRecommendationStatus() {
        //TODO
    }
}
