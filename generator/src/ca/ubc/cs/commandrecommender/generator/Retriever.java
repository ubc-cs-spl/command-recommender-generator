package ca.ubc.cs.commandrecommender.generator;

/**
 * Created by KeEr on 2014-06-04.
 */

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.*;


public class Retriever {

    public static final String DB_NAME = "commands-development";
    public static final String COMMANDS_COLLECTION = "commands";
    public static final String RECOMMENDATION_COLLECTION = "recommendations";

    private static final String USER_ID = "user_id";
    private static final String KIND = "kind";
    private static final String COMMAND = "command";
    private static final String COMMAND_ID = "command_id";
    private static final String NEW = "new_recommendation";
    private static final String REASON = "reason";
    private static final String CREATED_ON = "created_on";
    private static final String DESCRIPTION = "description";
    private static final String ID = "_id";
    private static final String BINDING_USED = "bindingUsed";
    private static final String USEFUL = "useful";

    private static final String FREQUENT_REASON = "Most frequent commands which you are not using.";
    private static final String HOTKEY_REASON = "You have never used hot-key to trigger this command.";
    

    //Currently get the most frequently used command that a person doesn't know
    public static void main( String args[] ) throws UnknownHostException {
        String user = args[0];

        //establish connection
        MongoClient mongoClient = new MongoClient();

        List<String> sortedCmds = getCmdsSortedByFrequency(mongoClient);
        Set<String> knownCmds = new HashSet<String>();
        knownCmds.addAll(getUsedCmdsForUser(user, mongoClient));
        knownCmds.addAll(getRecommendedCmdsForUser(user, mongoClient));
        markAllRecommendationOld(mongoClient, user);
        for (String recommendation : filterOut(sortedCmds, knownCmds, 7)) {
            insertRecommendation(recommendation,
                    FREQUENT_REASON,
                    user,
                    mongoClient);
        }
        for (String recommendation : getHotkeyRecommendations(mongoClient, user, 3)) {
            insertRecommendation(recommendation,
                    HOTKEY_REASON,
                    user,
                    mongoClient);
        }
    }

    private static List<String> getHotkeyRecommendations(MongoClient client,
                                                         String user, int amount) {
        DBCollection usageData = client.getDB(DB_NAME).getCollection(COMMANDS_COLLECTION);
        AggregationOutput commands = usageData.aggregate(neverHotkeyPipeline(user));
        Set<String> knownCmds = new HashSet<String>();
        for (DBObject entry : commands.results()) {
            knownCmds.add((String) entry.get(ID));
        }
        knownCmds.addAll(getRecommendedCmdsForUser(user, client));
        return filterOut(getUsedCmdsForUser(user, client), knownCmds, amount);
    }


    private static List<String> getCmdsSortedByFrequency(MongoClient client) {
        DBCollection usageData = client.getDB(DB_NAME).getCollection(COMMANDS_COLLECTION);
        AggregationOutput cmdByFrequency = usageData.aggregate(mostFrequentPipeline());
        List<String> commands = new ArrayList<String>();
        for (DBObject entry : cmdByFrequency.results()) {
            commands.add((String) entry.get(ID));
        }
        return commands;
    }

    private static Set<String> getUsedCmdsForUser(String user, MongoClient client) {
        DB cmdDev = client.getDB(DB_NAME);
        DBCollection usageData = cmdDev.getCollection(COMMANDS_COLLECTION);
        AggregationOutput knownCommands = usageData.aggregate(usedCmdPipeline(user));
        Set<String> knownCmds = new HashSet<String>();
        for (DBObject entry : knownCommands.results()) {
            knownCmds.add((String) entry.get(ID));
        }
        return knownCmds;
    }

    private static Set<String> getRecommendedCmdsForUser(String user, MongoClient client) {
        DB cmdDev = client.getDB(DB_NAME);
        DBCollection recommendations = cmdDev.getCollection(RECOMMENDATION_COLLECTION);
        Set<String> knownCmds = new HashSet<String>();
        DBObject query = new BasicDBObject(USER_ID, user).append(NEW, false);
        DBCursor recommendedCmds = recommendations.find(query, new BasicDBObject(COMMAND_ID, 1));
        for (DBObject entry : recommendedCmds) {
            knownCmds.add((String) entry.get(COMMAND_ID));
        }
        return knownCmds;
    }

    private static List<DBObject> neverHotkeyPipeline(String user) {
        DBObject matchFields = new BasicDBObject(USER_ID, user)
                .append(KIND, COMMAND)
                .append(BINDING_USED, true);
        DBObject match = new BasicDBObject("$match", matchFields);
        DBObject project = new BasicDBObject("$project", new BasicDBObject(DESCRIPTION, 1));
        DBObject group = new BasicDBObject("$group", new BasicDBObject(ID, "$description"));
        return Arrays.asList(match, project, group);
    }

    private static List<DBObject> usedCmdPipeline(String user) {
        DBObject matchFields = new BasicDBObject(USER_ID, user).append(KIND, COMMAND);
        DBObject match = new BasicDBObject("$match", matchFields);
        DBObject project = new BasicDBObject("$project", new BasicDBObject(DESCRIPTION, 1));
        DBObject group = new BasicDBObject("$group", new BasicDBObject(ID, "$description"));
        return Arrays.asList(match, project, group);
    }

    private static List<DBObject> mostFrequentPipeline() {
        String countField = "count";
        DBObject match = new BasicDBObject("$match", new BasicDBObject(KIND, COMMAND));
        DBObject project1 = new BasicDBObject("$project", new BasicDBObject(DESCRIPTION, 1));
        DBObject groupFields = new BasicDBObject(ID, "$description")
                .append(countField, new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject(countField, -1));
        DBObject project2 = new BasicDBObject("$project", new BasicDBObject(ID, 1));
        return Arrays.asList(match, project1, group, sort, project2);
    }

    private static List<String> filterOut(Collection<String> cmds,
                                          Collection<String> knownCmd, int amount) {
        List<String> results = new ArrayList<String>();
        int count = 0;
        for (String cmd : cmds) {
            if (count >= amount)
                break;
            if (!knownCmd.contains(cmd)) {
                results.add(cmd);
                count++;
            }
        }
        return results;
    }

    private static void insertRecommendation(String commandId, String reason,
                                             String user, MongoClient client) {
        DBCollection collection = client.getDB(DB_NAME)
                .getCollection(RECOMMENDATION_COLLECTION);
        DBObject recommendation = new BasicDBObject(USER_ID, user);
        recommendation.put(COMMAND_ID, commandId);
        recommendation.put(REASON, reason);
        recommendation.put(NEW, true);
        recommendation.put(CREATED_ON, new Date());
        recommendation.put(USEFUL, null);
        collection.insert(recommendation);
    }

    private static void markAllRecommendationOld(MongoClient client, String user) {
        DBCollection collection = client.getDB(DB_NAME)
                .getCollection(RECOMMENDATION_COLLECTION);
        BasicDBObject query = new BasicDBObject(NEW, true).append(USER_ID, user);
        BasicDBObject update = new BasicDBObject("$set" , new BasicDBObject(NEW, false));
        collection.update(query, update, false, true);
    }

}
