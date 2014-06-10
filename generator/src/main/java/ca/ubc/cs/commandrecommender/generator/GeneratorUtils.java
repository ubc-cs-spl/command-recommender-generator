package ca.ubc.cs.commandrecommender.generator;

import com.mongodb.*;

import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class GeneratorUtils {

    static List<String> getAllUsers(CmdDevDB db) {
        DBCollection usageData = db.getCommandsCollection();
        return usageData.distinct(CmdDevDB.USER_ID);
    }

    static Set<String> getUsedCmdsForUser(String user, CmdDevDB db) {
        DBCollection usageData = db.getCommandsCollection();
        DBObject query = new BasicDBObject(CmdDevDB.USER_ID, user)
                .append(CmdDevDB.KIND, CmdDevDB.COMMAND);
        List<String> usedCmds = usageData.distinct(CmdDevDB.DESCRIPTION, query);
        Set<String> knownCmds = new HashSet<String>(usedCmds);
        return knownCmds;
    }

    static Set<String> getAlreadyRecommendedCmdsForUser(String user, CmdDevDB db) {
        DBCollection collection = db.getRecommendationsCollection();
        DBObject query = new BasicDBObject(CmdDevDB.USER_ID, user);
        List<String> recommendedCmds = collection.distinct(CmdDevDB.COMMAND_ID, query);
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
                                     String user, CmdDevDB db) {
        DBCollection collection = db.getRecommendationsCollection();
        DBObject recommendation = new BasicDBObject(CmdDevDB.USER_ID, user);
        recommendation.put(CmdDevDB.COMMAND_ID, commandId);
        recommendation.put(CmdDevDB.REASON, reason);
        recommendation.put(CmdDevDB.NEW, true);
        recommendation.put(CmdDevDB.CREATED_ON, new Date());
        recommendation.put(CmdDevDB.USEFUL, null);
        collection.insert(recommendation);
    }

    static void markAllRecommendationOld(CmdDevDB db, String user) {
        DBCollection collection = db.getRecommendationsCollection();
        DBObject query = new BasicDBObject(CmdDevDB.NEW, true)
                .append(CmdDevDB.USER_ID, user);
        DBObject update = new BasicDBObject("$set",
                new BasicDBObject(CmdDevDB.NEW, false));
        collection.update(query, update, false, true);
    }
}
