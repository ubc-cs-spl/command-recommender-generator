package ca.ubc.cs.commandrecommender.db;

import com.mongodb.*;

import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class EclipseCmdDevDB implements IRecommenderDB {
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
    public static final String ID = "_id";
    public static final String BINDING_USED = "bindingUsed";
    public static final String USEFUL = "useful";
    public static final String SHORTCUT = "shortcut";

    public EclipseCmdDevDB(MongoClient client) {
        dbName = "commands-development";
        this.client = client;
    }

    public EclipseCmdDevDB(String dbName, MongoClient client) {
        this.dbName = dbName;
        this.client = client;
    }

    public DB getDB(){
        return client.getDB(dbName);
    }

    @Override
    public List<String> getCmdsSortedByFrequency() {
        DBCollection usageData = getCommandsCollection();
        AggregationOutput cmdByFrequency = usageData.aggregate(mostFrequentPipeline());
        List<String> commands = new ArrayList<String>();
        for (DBObject entry : cmdByFrequency.results()) {
            commands.add((String) entry.get(EclipseCmdDevDB.ID));
        }
        return commands;
    }

    @Override
    public List<String> getCmdsForWhichUserKnowsShortcut(String user) {
        DBObject query = new BasicDBObject(EclipseCmdDevDB.USER_ID, user)
                .append(EclipseCmdDevDB.KIND, EclipseCmdDevDB.COMMAND)
                .append(EclipseCmdDevDB.BINDING_USED, true);
        return getCommandsCollection().distinct(EclipseCmdDevDB.DESCRIPTION, query);
    }

    private static List<DBObject> mostFrequentPipeline() {
        String countField = "count";
        DBObject match = new BasicDBObject("$match",
                new BasicDBObject(EclipseCmdDevDB.KIND, EclipseCmdDevDB.COMMAND));
        DBObject project = new BasicDBObject("$project",
                new BasicDBObject(EclipseCmdDevDB.DESCRIPTION, 1));
        DBObject groupFields = new BasicDBObject(EclipseCmdDevDB.ID, "$description")
                .append(countField, new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject(countField, -1));
        DBObject project2 = new BasicDBObject("$project",
                new BasicDBObject(EclipseCmdDevDB.ID, 1));
        return Arrays.asList(match, project, group, sort, project2);
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
        return client.getDB(dbName).getCollection(collection);
    }

    @Override
    public List<String> getAllUsers() {
        DBCollection usageData = getCommandsCollection();
        return usageData.distinct(EclipseCmdDevDB.USER_ID);
    }

    @Override
    public Set<String> getCmdsWithShortcuts() {
        DBCollection details = getDetailsCollection();
        DBObject query = new BasicDBObject(EclipseCmdDevDB.SHORTCUT,
                new BasicDBObject("$ne", null));
        List<String> resultList = details.distinct(EclipseCmdDevDB.COMMAND_ID, query);
        return new HashSet<String>(resultList);
    }

    @Override
    public Set<String> getUsedCmdsForUser(String user) {
        DBCollection usageData = getCommandsCollection();
        DBObject query = new BasicDBObject(EclipseCmdDevDB.USER_ID, user)
                .append(EclipseCmdDevDB.KIND, EclipseCmdDevDB.COMMAND);
        List<String> usedCmds = usageData.distinct(EclipseCmdDevDB.DESCRIPTION, query);
        Set<String> knownCmds = new HashSet<String>(usedCmds);
        return knownCmds;
    }

    @Override
    public Set<String> getAlreadyRecommendedCmdsForUser(String user) {
        DBCollection collection = getRecommendationsCollection();
        DBObject query = new BasicDBObject(EclipseCmdDevDB.USER_ID, user);
        List<String> recommendedCmds = collection.distinct(EclipseCmdDevDB.COMMAND_ID, query);
        Set<String> knownCmds = new HashSet<String>(recommendedCmds);
        return knownCmds;
    }

    /**
     * Filter out knownCmd from cmds an keep only the first (amount) of recommendations
     * @param cmds possible recommendations
     * @param knownCmds cmds that should not be recommended
     * @param amount max number of recommendations to make
     * @return recommendations determined by the params
     */
    //TODO: move this out into another class
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

    @Override
    public void insertRecommendation(String commandId, String reason, String user) {
        DBCollection collection = getRecommendationsCollection();
        DBObject recommendation = new BasicDBObject();
        recommendation.put(EclipseCmdDevDB.USER_ID, user);
        recommendation.put(EclipseCmdDevDB.COMMAND_ID, commandId);
        recommendation.put(EclipseCmdDevDB.REASON, reason);
        recommendation.put(EclipseCmdDevDB.NEW, true);
        recommendation.put(EclipseCmdDevDB.CREATED_ON, new Date());
        recommendation.put(EclipseCmdDevDB.USEFUL, null);
        collection.insert(recommendation);
    }

    @Override
    public void markAllRecommendationOld(String user) {
        DBCollection collection = getRecommendationsCollection();
        DBObject query = new BasicDBObject(EclipseCmdDevDB.NEW, true)
                .append(EclipseCmdDevDB.USER_ID, user);
        DBObject update = new BasicDBObject("$set",
                new BasicDBObject(EclipseCmdDevDB.NEW, false));
        collection.update(query, update, false, true);
    }

    @Override
    public boolean shouldRecommendToUser(String user) {
        DBCollection collection = getUsersCollection();
        DBObject query = new BasicDBObject(EclipseCmdDevDB.USER_ID, user);
        DBObject userEntry = collection.findOne(query);
        Date lastUpload = (Date) userEntry.get(EclipseCmdDevDB.LAST_UPLOAD_DATE);
        Date lastRecommend = (Date) userEntry.get(EclipseCmdDevDB.LAST_RECOMMENDATION_DATE);
        if (lastUpload == null) {
            return false;
        } else if (lastRecommend == null) {
            return true;
        } else {
            return lastUpload.after(lastRecommend);
        }
    }

    @Override
    public void updateRecommendationStatus(String user) {
        DBCollection collection = getUsersCollection();
        DBObject query = new BasicDBObject(EclipseCmdDevDB.USER_ID, user);
        DBObject update = new BasicDBObject("$set",
                new BasicDBObject(EclipseCmdDevDB.LAST_RECOMMENDATION_DATE, new Date()));
        collection.update(query, update, true, false);
    }
}
