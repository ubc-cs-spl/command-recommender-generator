package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.db.IRecommenderDB;
import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;

import java.util.Set;

/**
 * Created by KeEr on 2014-06-09.
 */
public class HotkeyRecGen extends AbstractRecGen {

    //TODO: implement this class

    private Set<String> cmdsWithShortcuts;

    public HotkeyRecGen(IRecommenderDB db, String algorithm) {
        super(db, algorithm);
    }

    @Override
    public void fillRecommendations(RecommendationCollector rc) {

    }

    @Override
    public void trainWith(ToolUseCollection uses) {

    }

    @Override
    public void runAlgorithm() {

    }


/*
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
    */

}
