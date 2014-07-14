package ca.ubc.cs.commandrecommender.model;

import ca.ubc.cs.commandrecommender.db.AbstractRecommendationDB;

import java.util.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by KeEr on 2014-06-19.
 */
public class User {
    private String userId;
    private Date lastUpdate;
    private Date lastRecommendationDate;
    private HashSet<Integer> pastRecommendations;
    private AbstractRecommendationDB recommendationDB;
    private int WINDOW_IN_DAYS = 5;

    public User(String userId, Date lastUpdate, 
                Date lastRecommendationDate, HashSet<Integer> pastRecommendations, 
                AbstractRecommendationDB recommendationDB){
        this.userId = userId;
        this.lastUpdate = lastUpdate;
        this.pastRecommendations = pastRecommendations;
        this.recommendationDB = recommendationDB;
        this.lastRecommendationDate = lastRecommendationDate;
    }

    public String getUserId() {
        return userId;
    }

    public HashSet<Integer> getPastRecommendations() {
        return pastRecommendations;
    }

    public boolean isTimeToGenerateRecs() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -WINDOW_IN_DAYS);
        Date beginningOfWindow = new Date(now.getTimeInMillis());
        return lastUpdate.after(beginningOfWindow);
    }

    public void saveRecommendations(RecommendationCollector recommendations,
                                    String reason,
                                    String algorithmType,
                                    IndexMap toolIndexMap) {
        recommendationDB.markRecommendationsAsOld(userId);
        Map<Integer, Rationale> rationaleMap = recommendations.getRationales();
        for(Integer recommendation : recommendations){
            String commandId = toolIndexMap.getItemByIndex(recommendation);
            Rationale rationale = rationaleMap.get(recommendation);
            recommendationDB.saveRecommendation(commandId,
                    userId,
                    reason,
                    algorithmType,
                    rationale);
        }
    }


    public void updateRecommendationStatus() {
        recommendationDB.updateRecommendationStatus(userId);
    }
}
