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
                                                   MongoClient mongoClient,
                                                   List<String> sortedCmds,
                                                   int amount)
            throws UnknownHostException {
        Set<String> knownCmds = new HashSet<String>();
        knownCmds.addAll(GeneratorUtils.getUsedCmdsForUser(user, mongoClient));
        knownCmds.addAll(GeneratorUtils.getAlreadyRecommendedCmdsForUser(user, mongoClient));
        GeneratorUtils.markAllRecommendationOld(mongoClient, user);
        for (String recommendation : GeneratorUtils.filterOut(sortedCmds, knownCmds, amount)) {
            GeneratorUtils.insertRecommendation(recommendation,
                    DBUtils.FREQUENT_REASON,
                    user,
                    mongoClient);
        }
    }

    public static List<String> getCmdsSortedByFrequency(MongoClient client) {
        DBCollection usageData = DBUtils.getCommandsCollection(client);
        AggregationOutput cmdByFrequency = usageData.aggregate(mostFrequentPipeline());
        List<String> commands = new ArrayList<String>();
        for (DBObject entry : cmdByFrequency.results()) {
            commands.add((String) entry.get(DBUtils.ID));
        }
        return commands;
    }

    private static List<DBObject> mostFrequentPipeline() {
        String countField = "count";
        DBObject match = new BasicDBObject("$match",
                new BasicDBObject(DBUtils.KIND, DBUtils.COMMAND));
        DBObject project1 = new BasicDBObject("$project",
                new BasicDBObject(DBUtils.DESCRIPTION, 1));
        DBObject groupFields = new BasicDBObject(DBUtils.ID, "$description")
                .append(countField, new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject(countField, -1));
        DBObject project2 = new BasicDBObject("$project",
                new BasicDBObject(DBUtils.ID, 1));
        return Arrays.asList(match, project1, group, sort, project2);
    }

}
