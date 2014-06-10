package ca.ubc.cs.commandrecommender.generator;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class MostUsedRecGen {

    //Currently get the most frequently used command that a person doesn't know
    public static void updateRecommendationForUser(String user,
                                                   CmdDevDB db,
                                                   List<String> sortedCmds,
                                                   int amount)
            throws UnknownHostException {
        Set<String> knownCmds = new HashSet<String>();
        knownCmds.addAll(GeneratorUtils.getUsedCmdsForUser(user, db));
        knownCmds.addAll(GeneratorUtils.getAlreadyRecommendedCmdsForUser(user, db));
        GeneratorUtils.markAllRecommendationOld(db, user);
        for (String recommendation : GeneratorUtils.filterOut(sortedCmds, knownCmds, amount)) {
            GeneratorUtils.insertRecommendation(recommendation,
                    CmdDevDB.FREQUENT_REASON,
                    user,
                    db);
        }
    }

    public static List<String> getCmdsSortedByFrequency(CmdDevDB db) {
        DBCollection usageData = db.getCommandsCollection();
        AggregationOutput cmdByFrequency = usageData.aggregate(mostFrequentPipeline());
        List<String> commands = new ArrayList<String>();
        for (DBObject entry : cmdByFrequency.results()) {
            commands.add((String) entry.get(CmdDevDB.ID));
        }
        return commands;
    }

    private static List<DBObject> mostFrequentPipeline() {
        String countField = "count";
        DBObject match = new BasicDBObject("$match",
                new BasicDBObject(CmdDevDB.KIND, CmdDevDB.COMMAND));
        DBObject project = new BasicDBObject("$project",
                new BasicDBObject(CmdDevDB.DESCRIPTION, 1));
        DBObject groupFields = new BasicDBObject(CmdDevDB.ID, "$description")
                .append(countField, new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject(countField, -1));
        DBObject project2 = new BasicDBObject("$project",
                new BasicDBObject(CmdDevDB.ID, 1));
        return Arrays.asList(match, project, group, sort, project2);
    }

}
