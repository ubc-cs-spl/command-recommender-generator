package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.db.EclipseCmdDevDB;
import ca.ubc.cs.commandrecommender.db.IRecommenderDB;

import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class HotkeyRecGen extends AbstractGen {

    private static final String REASON = "You have never used hot-key to trigger this command.";

    private Set<String> cmdsWithShortcuts;

    public HotkeyRecGen(IRecommenderDB db) {
        super(db, REASON);
        cmdsWithShortcuts = db.getCmdsWithShortcuts();
    }

    public List<String> getRecommendationsForUser(String user, int amount) {
        Set<String> knownCmds = new HashSet<String>(db.getCmdsForWhichUserKnowsShortcut(user));
        knownCmds.addAll(db.getAlreadyRecommendedCmdsForUser(user));
        Set<String> possibleRecommendations = db.getUsedCmdsForUser(user);
        possibleRecommendations.retainAll(cmdsWithShortcuts);
        return EclipseCmdDevDB.filterOut(possibleRecommendations, knownCmds, amount);
    }

}
