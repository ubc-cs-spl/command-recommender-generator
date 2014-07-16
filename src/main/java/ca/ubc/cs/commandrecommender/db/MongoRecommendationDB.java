package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.Rationale;
import ca.ubc.cs.commandrecommender.model.User;
import com.google.common.primitives.Ints;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Adapter for the recommendation, user, and command details collections in mongoDB
 *
 * Created by Spencer on 6/23/2014.
 */
public class MongoRecommendationDB extends AbstractRecommendationDB{
    public static final String USER_ID_FIELD = "user_id";
    public static final String COMMAND_DETAIL_ID_FIELD = "command_detail_id";
    public static final String COMMAND_DETAIL_OBJECT_ID_FIELD = "_id";
    public static final String USER_COLLECTION = "users";
    public static final String USER_RECOMMENDATION_COLLECTION = "recommendations";
    public static final String COMMAND_DETAILS_COLLECTION = "command_details";
    public static final String COMMAND_ID_FIELD = "command_id";
    public static final String LAST_UPLOADED_DATE_FIELD = "last_upload_date";
    public static final String LAST_RECOMMENDATION_DATE_FIELD = "last_recommendation_date";
    public static final String LAST_RECOMMENDATION_ALGORITHM_FIELD = "last_recommendation_algorithm";
    public static final String CREATED_ON = "created_on";
    public static final String REASON_FIELD = "reason";
    public static final String ALGORITHM_TYPE_FIELD = "algorithm_type";
    public static final String ALGORITHM_VALUE_FIELD = "algorithm_value";
    public static final String REASON_VALUE_FIELD = "reason_value";
    public static final String RANK_FIELD = "rank";

    private MongoClient recommendationClient;
    private DBCollection userCollection;
    private DBCollection recommendationCollection;
    private DBCollection commandDetailsCollection;


    public MongoRecommendationDB(ConnectionParameters connectionParameters, IndexMap userIndexMap)
            throws DBConnectionException{
        super(userIndexMap);
        try {
            recommendationClient = new MongoClient(connectionParameters.getDbUrl(),
                    connectionParameters.getDbPort());
            this.userCollection = getCollection(connectionParameters, USER_COLLECTION);
            this.recommendationCollection = getCollection(connectionParameters, USER_RECOMMENDATION_COLLECTION);
            this.commandDetailsCollection = getCollection(connectionParameters, COMMAND_DETAILS_COLLECTION);
            ensureIndex();
        }catch(UnknownHostException ex){
            throw new DBConnectionException(ex);
        }
    }

    private void ensureIndex() {
        if(recommendationCollection != null) {
        	DBObject compoundIndex = new BasicDBObject();
        	compoundIndex.put(USER_ID_FIELD, 1);
        	compoundIndex.put(ALGORITHM_TYPE_FIELD, 1);
        	compoundIndex.put(COMMAND_ID_FIELD, 1);
            recommendationCollection.createIndex(compoundIndex);
        }
    }

    private DBCollection getCollection(ConnectionParameters connectionParameters, String collection) {
        return recommendationClient.getDB(connectionParameters.getdBName()).getCollection(collection);
    }
    
    @Override
    public int getNumberOfKnownCommands() {
    	return Ints.saturatedCast(commandDetailsCollection.count());
    }

    @Override
    public void saveRecommendation(String commandId,
                                   String userId,
                                   String reason,
                                   String algorithmType,
                                   Rationale rationale) {
        if(commandId == null || commandId.equals("") || userId == null || userId.equals(""))
            return;
        DBObject queryForOldRecommendation = new BasicDBObject(USER_ID_FIELD, userId)
        .append(ALGORITHM_TYPE_FIELD, algorithmType)
        .append(COMMAND_ID_FIELD, commandId);
        DBObject recommendation = recommendationCollection.findOne(queryForOldRecommendation);
        
        if (recommendation == null) { // we never recommended it before
            DBObject commandDetail = commandDetailsCollection.findOne(new BasicDBObject(COMMAND_ID_FIELD, commandId));
            // If the command detail is not know, we would not make the recommendation for the user
            // This situation should not occur for the production version as all the tools we know of must be in the 
            // command detail table
            if(commandDetail == null)
            	return;
            recommendation =  new BasicDBObject(USER_ID_FIELD, userId)
            .append(ALGORITHM_TYPE_FIELD, algorithmType)
            .append(COMMAND_ID_FIELD, commandId)
            .append(COMMAND_DETAIL_ID_FIELD, commandDetail.get(COMMAND_DETAIL_OBJECT_ID_FIELD));
        }
        
        recommendation.put(RANK_FIELD, rationale.getRank());
        recommendation.put(ALGORITHM_VALUE_FIELD, rationale.getDecisionPointValue());
        recommendation.put(CREATED_ON, new Date(System.currentTimeMillis())); //TODO: should we keep this?
        recommendation.put(REASON_VALUE_FIELD, rationale.getValueForTypeSpecificReason()); //TODO: avoid duplication
        recommendation.put(REASON_FIELD, reason); //TODO: avoid duplication
        recommendationCollection.save(recommendation);
    }

    @Override
    public List<User> getAllUsers() {
        DBCursor userCursor = userCollection.find();
        List<User> users = new ArrayList<User>();
        while(userCursor.hasNext()){
            DBObject userDbObject = userCursor.next();
            String userId = (String) userDbObject.get(USER_ID_FIELD);
            Date lastUpdate = (Date) userDbObject.get(LAST_UPLOADED_DATE_FIELD);
            Date lastRecommendationDate = (Date) userDbObject.get(LAST_RECOMMENDATION_DATE_FIELD);
            users.add(new User(userId, lastUpdate, lastRecommendationDate, this));
        }
        return users;
    }

    @Override
    public void updateRecommendationStatus(String userId, String algoType) {
        DBObject query = new BasicDBObject(USER_ID_FIELD, userId);
        DBObject updatedValues = new BasicDBObject(LAST_RECOMMENDATION_DATE_FIELD, new Date())
        .append(LAST_RECOMMENDATION_ALGORITHM_FIELD, algoType);
        userCollection.update(query, new BasicDBObject("$set", updatedValues), true, false);
    }

	@Override
	public void clearInfoAndRankings(String userId, String algoType) {
        DBObject query = new BasicDBObject(USER_ID_FIELD, userId).append(ALGORITHM_TYPE_FIELD, algoType);
        DBObject fieldsToClear = new BasicDBObject(ALGORITHM_VALUE_FIELD, null)
        .append(RANK_FIELD, null)
        .append(REASON_VALUE_FIELD, null);
		recommendationCollection.update(query, new BasicDBObject("$unset", fieldsToClear), false, true);
	}
    
}
