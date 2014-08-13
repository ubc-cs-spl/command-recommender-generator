package ca.ubc.cs.commandrecommender.model;

import ca.ubc.cs.commandrecommender.db.AbstractRecommendationDB;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by KeEr on 2014-06-19.
 */
public class User {
    private String userId;
    private Date lastUpdate;
    private Date lastRecommendationDate;
    private AbstractRecommendationDB recommendationDB;
    private int WINDOW_IN_DAYS = 5;

    public User(String userId, Date lastUpdate, 
                Date lastRecommendationDate, 
                AbstractRecommendationDB recommendationDB){
        this.userId = userId;
        this.lastUpdate = lastUpdate;
        this.recommendationDB = recommendationDB;
        this.lastRecommendationDate = lastRecommendationDate;
    }

    public String getUserId() {
        return userId;
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
        recommendationDB.saveRecommendations(recommendations,
                userId, reason, algorithmType, toolIndexMap);
    }
    
}
