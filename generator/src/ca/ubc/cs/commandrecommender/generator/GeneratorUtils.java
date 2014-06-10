package ca.ubc.cs.commandrecommender.generator;

import com.mongodb.*;

import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class GeneratorUtils {

    static List<String> getAllUsers(MongoClient client) {
        DBCollection usageData = DBUtils.getCommandsCollection(client);
        return usageData.distinct(DBUtils.USER_ID);
    }

    static Set<String> getUsedCmdsForUser(String user, MongoClient client) {
        DBCollection usageData = DBUtils.getCommandsCollection(client);
        DBObject query = new BasicDBObject(DBUtils.USER_ID, user)
                .append(DBUtils.KIND, DBUtils.COMMAND);
        List<String> usedCmds = usageData.distinct(DBUtils.DESCRIPTION, query);
        Set<String> knownCmds = new HashSet<String>(usedCmds);
        return knownCmds;
    }

    static Set<String> getAlreadyRecommendedCmdsForUser(String user, MongoClient client) {
        DBCollection collection = DBUtils.getRecommendationsCollection(client);
        DBObject query = new BasicDBObject(DBUtils.USER_ID, user);
        List<String> recommendedCmds = collection.distinct(DBUtils.COMMAND_ID, query);
        Set<String> knownCmds = new HashSet<String>(recommendedCmds);
        return knownCmds;
    }

    static List<String> filterOut(Collection<String> cmds,
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

    static void insertRecommendation(String commandId, String reason,
                                     String user, MongoClient client) {
        DBCollection collection = DBUtils.getRecommendationsCollection(client);
        DBObject recommendation = new BasicDBObject(DBUtils.USER_ID, user);
        recommendation.put(DBUtils.COMMAND_ID, commandId);
        recommendation.put(DBUtils.REASON, reason);
        recommendation.put(DBUtils.NEW, true);
        recommendation.put(DBUtils.CREATED_ON, new Date());
        recommendation.put(DBUtils.USEFUL, null);
        collection.insert(recommendation);
    }

    static void markAllRecommendationOld(MongoClient client, String user) {
        DBCollection collection = DBUtils.getRecommendationsCollection(client);
        DBObject query = new BasicDBObject(DBUtils.NEW, true)
                .append(DBUtils.USER_ID, user);
        DBObject update = new BasicDBObject("$set",
                new BasicDBObject(DBUtils.NEW, false));
        collection.update(query, update, false, true);
    }
}
