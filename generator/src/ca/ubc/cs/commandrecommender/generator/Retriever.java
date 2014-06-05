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

    //Currently get the most frequently used command that a person doesn't know
    public static void main( String args[] ) throws UnknownHostException {
        String user = args[0];
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient();
        // Now connect to your databases
        DB cmdDev = mongoClient.getDB( DB_NAME );
        DBCollection usageData = cmdDev.getCollection(COMMANDS_COLLECTION);
        DBCollection recommendations = cmdDev.getCollection(RECOMMENDATION_COLLECTION);
        List<String> sortedCmds = getCmdsSortedByFrequency(usageData);
        Set<String> knownCmds = getKnownCmdsForUser(user, usageData, recommendations);
        markAllRecommendationOld(mongoClient);
        for (String recommendation : filterOut(sortedCmds, knownCmds, 10)) {
            insertRecommendation(recommendation,
                    "Most frequent commands which you are not using.",
                    user,
                    recommendations);
        }
    }

    private static List<String> getCmdsSortedByFrequency(DBCollection usageData) {
        AggregationOutput cmdByFrequency = usageData.aggregate(mostFrequentPipeline());
        List<String> commands = new ArrayList<String>();
        for (DBObject entry : cmdByFrequency.results()) {
            commands.add((String) entry.get("_id"));
        }
        return commands;
    }

    private static Set<String> getKnownCmdsForUser(String user, DBCollection usageData, DBCollection recommendations) {
        AggregationOutput knownCommands = usageData.aggregate(knownCmdPipeline(user));
        Set<String> knownCmds = new HashSet<String>();
        for (DBObject entry : knownCommands.results()) {
            knownCmds.add((String) entry.get("_id"));
        }
        DBObject querry = new BasicDBObject("user_id", user);
        querry.put("new", false);
        DBCursor recommendedCmds = recommendations.find(querry, new BasicDBObject("command_id",1));
        for (DBObject entry : recommendedCmds) {
            knownCmds.add((String) entry.get("command_id"));
        }
        return knownCmds;
    }

    private static List<DBObject> knownCmdPipeline(String user) {
        DBObject matchFields = new BasicDBObject("user_id", user).append("kind", "command");
        DBObject match = new BasicDBObject("$match", matchFields);
        DBObject project = new BasicDBObject("$project", new BasicDBObject("description", 1));
        DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$description"));
        return Arrays.asList(match, project, group);
    }

    private static List<DBObject> mostFrequentPipeline() {
        DBObject match = new BasicDBObject("$match", new BasicDBObject("kind", "command"));
        DBObject project1 = new BasicDBObject("$project", new BasicDBObject("description", 1));
        DBObject groupFields = new BasicDBObject("_id", "$description")
                .append("count", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("count", -1));
        //DBObject limit = new BasicDBObject("$limit", num);
        DBObject project2 = new BasicDBObject("$project", new BasicDBObject("_id", 1));
        return Arrays.asList(match, project1, group, sort, project2);
    }

    private static List<String> filterOut(List<String> cmds, Collection<String> knownCmd, int amount) {
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

    private static void insertRecommendation(String commandId, String reason, String user, DBCollection recommendations) {
        DBObject recommendation = new BasicDBObject("user_id", user);
        recommendation.put("command_id", commandId);
        recommendation.put("reason", reason);
        recommendation.put("new", true);
        recommendation.put("time_of_creation", System.currentTimeMillis());
        recommendations.insert(recommendation);
    }

    private static void markAllRecommendationOld(MongoClient client) {
        DBCollection collection = client.getDB(DB_NAME).getCollection(RECOMMENDATION_COLLECTION);
        BasicDBObject query = new BasicDBObject("new", true);
        BasicDBObject update = new BasicDBObject("$set" , new BasicDBObject("new", false));
        collection.update(query, update, false, true);
    }
}
