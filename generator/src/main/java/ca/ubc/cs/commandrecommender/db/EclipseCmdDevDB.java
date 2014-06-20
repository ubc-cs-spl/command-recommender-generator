package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import ca.ubc.cs.commandrecommender.model.User;
import com.mongodb.*;

import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class EclipseCmdDevDB implements IRecommenderDB {

    //TODO: close connections properly?

    //=====================================================================================
    //TODO: pick out what's needed
    private final String dbName;
    private final MongoClient client;
    public static final String COMMANDS_COLLECTION = "commands";
    public static final String RECOMMENDATIONS_COLLECTION = "recommendations";
    public static final String USERS_COLLECTION = "users";
    public static final String DETAILS_COLLECTION = "command_details";
    public static final String LAST_RECOMMENDATION_DATE = "last_recommendation_date";
    public static final String LAST_UPLOAD_DATE = "last_upload_date";
    public static final String USER_ID = "user_id";
    public static final String KIND = "kind";
    public static final String COMMAND = "command";
    public static final String COMMAND_ID = "command_id";
    public static final String NEW = "new_recommendation";
    public static final String REASON = "reason";
    public static final String CREATED_ON = "created_on";
    public static final String DESCRIPTION = "description";
    public static final String BINDING_USED = "bindingUsed";
    public static final String USEFUL = "useful";
    public static final String SHORTCUT = "shortcut";
    public static final String COMMAND_DETAIL_ID = "command_detail_id";
    //=======================================================================================
    // THESE ARE NEW AND INCOMPLETE!!

    public EclipseCmdDevDB(MongoClient client) {
        dbName = "commands-development";
        this.client = client;
    }

    //TODO: This constructor is for testing purposes only.
    //      What would be a better way to test?
    public EclipseCmdDevDB(String dbName, MongoClient client) {
        this.dbName = dbName;
        this.client = client;
    }

    @Override
    public List<ToolUseCollection> getAllData() {
        return null; //TODO
    }

    @Override
    public void insureIndex() {
        //TODO: add in index that should now be created
        //TODO: delete the index we no longer need
        DBCollection usageData = getCommandsCollection();
        usageData.createIndex(new BasicDBObject(USER_ID, 1).append(KIND, 1));
        DBCollection users = getUsersCollection();
        users.createIndex(new BasicDBObject(USER_ID, 1));
        DBCollection details = getDetailsCollection();
        details.createIndex(new BasicDBObject(COMMAND_ID, 1));
        DBCollection recommendations = getRecommendationsCollection();
        recommendations.createIndex(new BasicDBObject(USER_ID, 1));
    }

    @Override
    public String getCmdId(int cmdCode) {
        return null; //TODO
    }

    @Override
    public Set<Integer> getCmdsForWhichUserKnowsShortcut(User user) {
        DBObject query = new BasicDBObject(USER_ID, user.getUserId())
                .append(KIND, COMMAND)
                .append(BINDING_USED, true);
        List<String> cmdIds = getCommandsCollection().distinct(DESCRIPTION, query);
        return cmdStringsToCmdCodes(cmdIds);
    }

    @Override
    public List<User> getAllUsers() {
        return null; //TODO
    }

    @Override
    public Set<Integer> getCmdsWithShortcuts() {
        //TODO modify as needed; this is remove old code
        DBCollection details = getDetailsCollection();
        DBObject query = new BasicDBObject(SHORTCUT,
                new BasicDBObject("$ne", null));
        List<String> resultList = details.distinct(COMMAND_ID, query);
        return cmdStringsToCmdCodes(resultList);
    }

    private Set<Integer> cmdStringsToCmdCodes(Collection<String> cmdIds) {
        return null; //TODO
    }

    //========================================================================================
    //TODO: pick out what's needed

    protected DB getDB(){
        return client.getDB(dbName);
    }

    protected DBCollection getDetailsCollection() {
        return  getCollection(DETAILS_COLLECTION);
    }

    protected DBCollection getCommandsCollection() {
        return getCollection(COMMANDS_COLLECTION);
    }

    protected DBCollection getRecommendationsCollection() {
        return getCollection(RECOMMENDATIONS_COLLECTION);
    }

    protected DBCollection getUsersCollection() {
        return getCollection(USERS_COLLECTION);
    }

    protected DBCollection getCollection(String collection) {
        return getDB().getCollection(collection);
    }
    //==============================================================================================
    //TODO: move this out into another class or remove it completely

    /**
     * Filter out knownCmd from cmds an keep only the first (amount) of recommendations
     * @param cmds possible recommendations
     * @param knownCmds cmds that should not be recommended
     * @param amount max number of recommendations to make
     * @return recommendations determined by the params
     */

    public static List<String> filterOut(Collection<String> cmds,
                                  Collection<String> knownCmds, int amount) {
        List<String> results = new ArrayList<String>();
        int count = 0;
        for (String cmd : cmds) {
            if (count >= amount)
                break;
            if (!knownCmds.contains(cmd)) {
                results.add(cmd);
                count++;
            }
        }
        return results;
    }

    //==============================================================================================
    //TODO: move the following into the User class?

    public Set<String> getAlreadyRecommendedCmdsForUser(String user) {
        DBCollection collection = getRecommendationsCollection();
        DBObject query = new BasicDBObject(USER_ID, user);
        List<String> recommendedCmds = collection.distinct(COMMAND_ID, query);
        Set<String> knownCmds = new HashSet<String>(recommendedCmds);
        return knownCmds;
    }

    public void insertRecommendation(String commandId, String reason, String user) {
        DBObject command = new BasicDBObject(COMMAND_ID, commandId);
        DBCollection commandDetails = getDetailsCollection();
        DBObject object = commandDetails.findOne(command);
        DBCollection collection = getRecommendationsCollection();
        if (object == null) {
            commandDetails.insert(command);
            object = commandDetails.findOne(command);
        }
        DBObject recommendation = new BasicDBObject();
        recommendation.put(COMMAND_DETAIL_ID, object.get("_id"));
        recommendation.put(USER_ID, user);
        recommendation.put(COMMAND_ID, commandId);
        recommendation.put(REASON, reason);
        recommendation.put(NEW, true);
        recommendation.put(CREATED_ON, new Date());
        recommendation.put(USEFUL, null);
        collection.insert(recommendation);
    }


    public void markAllRecommendationOld(String user) {
        DBCollection collection = getRecommendationsCollection();
        DBObject query = new BasicDBObject(NEW, true)
                .append(USER_ID, user);
        DBObject update = new BasicDBObject("$set",
                new BasicDBObject(NEW, false));
        collection.update(query, update, false, true);
    }


    public boolean shouldRecommendToUser(String user) {
        DBCollection collection = getUsersCollection();
        DBObject query = new BasicDBObject(USER_ID, user);
        DBObject userEntry = collection.findOne(query);
        Date lastUpload = (Date) userEntry.get(LAST_UPLOAD_DATE);
        Date lastRecommend = (Date) userEntry.get(LAST_RECOMMENDATION_DATE);
        if (lastUpload == null) {
            return false;
        } else if (lastRecommend == null) {
            return true;
        } else {
            return lastUpload.after(lastRecommend);
        }
    }


    public void updateRecommendationStatus(String user) {
        DBCollection collection = getUsersCollection();
        DBObject query = new BasicDBObject(USER_ID, user);
        DBObject update = new BasicDBObject("$set",
                new BasicDBObject(LAST_RECOMMENDATION_DATE, new Date()));
        collection.update(query, update, true, false);
    }
    //=======================================================================================

}
