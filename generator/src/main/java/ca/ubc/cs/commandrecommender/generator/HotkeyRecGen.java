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
                                                   CmdDevDB db,
                                                   int amount)
            throws UnknownHostException {
        GeneratorUtils.markAllRecommendationOld(db, user);
        for (String recommendation : getHotkeyRecommendations(db, user, amount)) {
            GeneratorUtils.insertRecommendation(recommendation,
                    CmdDevDB.HOTKEY_REASON,
                    user,
                    db);
        }
    }

    private static List<String> getHotkeyRecommendations(CmdDevDB db,
                                                         String user, int amount) {
        DBCollection usageData = db.getCommandsCollection();
        DBObject query = new BasicDBObject(CmdDevDB.USER_ID, user)
                .append(CmdDevDB.KIND, CmdDevDB.COMMAND)
                .append(CmdDevDB.BINDING_USED, true);
        List<String> hotkeyCmds = usageData.distinct(CmdDevDB.DESCRIPTION, query);
        Set<String> knownCmds = new HashSet<String>(hotkeyCmds);
        knownCmds.addAll(GeneratorUtils.getAlreadyRecommendedCmdsForUser(user, db));
        return GeneratorUtils.filterOut(GeneratorUtils.getUsedCmdsForUser(user, db), knownCmds, amount);
    }

}
