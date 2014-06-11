package ca.ubc.cs.commandrecommender.generator;

import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class HotkeyRecGen extends AbstractGen {

    private Set<String> cmdsWithShortcuts;

    public HotkeyRecGen(EclipseCmdDevDB db) {
        super(db);
        cmdsWithShortcuts = db.getCmdsWithShortcuts();
    }

    //Currently get the most frequently used command that a person doesn't know
    public void updateRecommendationForUser(String user,
                                                   int amount) {
        db.markAllRecommendationOld(user);
        for (String recommendation : getHotkeyRecommendations(user, amount)) {
            db.insertRecommendation(recommendation, EclipseCmdDevDB.HOTKEY_REASON, user);
        }
    }

    private List<String> getHotkeyRecommendations(String user, int amount) {
        Set<String> knownCmds = new HashSet<String>(db.getCmdsWithShortcutUserUse(user));
        knownCmds.addAll(db.getAlreadyRecommendedCmdsForUser(user));
        Set<String> possibleRecommendations = db.getUsedCmdsForUser(user);
        possibleRecommendations.retainAll(cmdsWithShortcuts);
        return EclipseCmdDevDB.filterOut(possibleRecommendations, knownCmds, amount);
    }

}
