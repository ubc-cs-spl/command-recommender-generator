package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.RecommenderOptions;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.Rationale;
import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.User;
import com.google.common.primitives.Ints;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Adapter for the recommendation, user, and command details collections in mongoDB
 *
 * Created by Spencer on 6/23/2014.
 */
public class MongoRecommendationDB extends AbstractRecommendationDB{
    public static final String USER_ID_FIELD = "user_id";
    public static final String COMMAND_DETAIL_ID_FIELD = "command_detail_id";
    public static final String COMMAND_DETAIL_OBJECT_ID_FIELD = "_id";
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
    private DBCollection commandDetailsCollection;
    private ConnectionParameters connectionParameters;

    protected DBCollection userCollection;
    protected DBCollection recommendationCollection;
    protected Map<String, ObjectId> commandDetailsMap;

    public MongoRecommendationDB(RecommenderOptions options, IndexMap userIndexMap)
            throws DBConnectionException{
        super(userIndexMap);
        try {
            this.connectionParameters = options.getRecommendationConnectionParamters();
            ServerAddress serverAddress = new ServerAddress(connectionParameters.getDbUrl(),
                    connectionParameters.getDbPort());
            if(!connectionParameters.getDbUser().equals("")){
                List<MongoCredential> credentialList = createCredentialList(connectionParameters);
                recommendationClient = new MongoClient(serverAddress, credentialList);
            }else{
                recommendationClient = new MongoClient(serverAddress);
            }
            userCollection = getCollection(options.getUserTable());
            recommendationCollection = getCollection(options.getRecommendationTable());
            commandDetailsCollection = getCollection(options.getCommandDetailTable());
            initCommandDetailsMap();
            ensureIndex();
        }catch(UnknownHostException ex){
            throw new DBConnectionException(ex);
        }
    }

    private List<MongoCredential> createCredentialList(
            ConnectionParameters connectionParameters) {
        MongoCredential userCredential = MongoCredential.createMongoCRCredential(
                connectionParameters.getDbUser(),
                connectionParameters.getdBName(),
                connectionParameters.getDbPassword().toCharArray());
        return Collections.singletonList(userCredential);
    }

    private void ensureIndex() {
        if(recommendationCollection != null) {
            DBObject compoundIndex = new BasicDBObject();
            compoundIndex.put(USER_ID_FIELD, 1);
            compoundIndex.put(ALGORITHM_TYPE_FIELD, 1);
            compoundIndex.put(COMMAND_ID_FIELD, 1);
            recommendationCollection.createIndex(compoundIndex);
        }
        if(userCollection != null) {
            userCollection.createIndex(new BasicDBObject("user_id", 1));
        }
    }

    private void initCommandDetailsMap() {
        commandDetailsMap = new HashMap<String, ObjectId>();
        DBCursor details = commandDetailsCollection.find();
        for (DBObject detail : details) {
            String commandId = (String) detail.get(COMMAND_ID_FIELD);
            ObjectId objectId = (ObjectId) detail.get(COMMAND_DETAIL_OBJECT_ID_FIELD);
            commandDetailsMap.put(commandId, objectId);
        }
    }

    protected DBCollection getCollection(String collection) {
        return recommendationClient.getDB(connectionParameters.getdBName()).getCollection(collection);
    }

    public void closeConnection() {
        recommendationClient.close();
    }

    @Override
    public int getNumberOfKnownCommands() {
        return Ints.saturatedCast(commandDetailsCollection.count());
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

    private void clearInfoAndRankings(String userId, String algoType) {
        DBObject query = new BasicDBObject(USER_ID_FIELD, userId).append(ALGORITHM_TYPE_FIELD, algoType);
        DBObject fieldsToClear = new BasicDBObject(RANK_FIELD, null);
        recommendationCollection.update(query, new BasicDBObject("$set", fieldsToClear), false, true);
    }

    @Override
    public void saveRecommendations(RecommendationCollector recommendations,
                                    String userId,
                                    String reason,
                                    String algorithmType,
                                    IndexMap toolIndexMap) {
        if (StringUtils.isEmpty(userId)) {
            return;
        }
        //TODO: consider clearing out only the old recommendations that aren't present anymore
        clearInfoAndRankings(userId, algorithmType);
        Map<Integer, Rationale> rationaleMap = recommendations.getRationales();
        BulkWriteOperation operation = recommendationCollection.initializeUnorderedBulkOperation();
        boolean hasOperation = false;
        for (Integer recommendation : recommendations) {
            String commandId = toolIndexMap.getItemByIndex(recommendation);
            Rationale rationale = rationaleMap.get(recommendation);
            hasOperation = saveRecommendation(commandId, userId, reason, algorithmType, rationale, operation)
                    || hasOperation;
        }
        if (hasOperation) {
            operation.execute();
        }
    }

    /**
     *
     * @param commandId
     * @param userId
     * @param reason
     * @param algorithmType
     * @param rationale
     * @param operation
     * @return whether an operation is added to the bulk operation
     */
    private boolean saveRecommendation(String commandId,
                                       String userId,
                                       String reason,
                                       String algorithmType,
                                       Rationale rationale,
                                       BulkWriteOperation operation) {
        if (StringUtils.isEmpty(commandId)) {
            return false;
        }
        ObjectId commandDetail = commandDetailsMap.get(commandId);
        if (commandDetail == null) {
            return false;
        }
        DBObject targetEntry = new BasicDBObject(USER_ID_FIELD, userId)
                .append(ALGORITHM_TYPE_FIELD, algorithmType)
                .append(COMMAND_ID_FIELD, commandId);
        DBObject fieldsToUpdate = new BasicDBObject(RANK_FIELD, rationale.getRank())
                .append(ALGORITHM_VALUE_FIELD, rationale.getDecisionPointValue())
                .append(CREATED_ON, new Date(System.currentTimeMillis())) // TODO: maybe remove this?
                .append(REASON_VALUE_FIELD, rationale.getValueForTypeSpecificReason())
                .append(REASON_FIELD, reason); //TODO avoid duplication
        DBObject fieldsToInitiate = new BasicDBObject(COMMAND_DETAIL_ID_FIELD, commandDetail);
        DBObject update = new BasicDBObject("$set", fieldsToUpdate)
                .append("$setOnInsert", fieldsToInitiate);
        operation.find(targetEntry).upsert().updateOne(update);
        return true;
    }


}
