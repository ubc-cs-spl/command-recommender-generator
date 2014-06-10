package ca.ubc.cs.commandrecommender.generator;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class HotkeyRecGen {

    //Currently get the most frequently used command that a person doesn't know
    public static void updateRecommendationForUser(String user,
                                                   MongoClient mongoClient,
                                                   int amount)
            throws UnknownHostException {
        GeneratorUtils.markAllRecommendationOld(mongoClient, user);
        for (String recommendation : getHotkeyRecommendations(mongoClient, user, amount)) {
            GeneratorUtils.insertRecommendation(recommendation,
                    DBUtils.HOTKEY_REASON,
                    user,
                    mongoClient);
        }
    }

    private static List<String> getHotkeyRecommendations(MongoClient client,
                                                         String user, int amount) {
        DBCollection usageData = DBUtils.getCommandsCollection(client);
        DBObject query = new BasicDBObject(DBUtils.USER_ID, user)
                .append(DBUtils.KIND, DBUtils.COMMAND)
                .append(DBUtils.BINDING_USED, true);
        List<String> hotkeyCmds = usageData.distinct(DBUtils.DESCRIPTION, query);
        Set<String> knownCmds = new HashSet<String>(hotkeyCmds);
        knownCmds.addAll(GeneratorUtils.getAlreadyRecommendedCmdsForUser(user, client));
        return GeneratorUtils.filterOut(GeneratorUtils.getUsedCmdsForUser(user, client), knownCmds, amount);
    }

}
