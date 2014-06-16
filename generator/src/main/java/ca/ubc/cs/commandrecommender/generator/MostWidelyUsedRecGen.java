package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.db.EclipseCmdDevDB;
import ca.ubc.cs.commandrecommender.db.IRecommenderDB;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by KeEr on 2014-06-13.
 */
public class MostWidelyUsedRecGen extends AbstractRecGen {

    public static final String REASON = "Most widely used";

    private final List<String> sortedCmds;

    public MostWidelyUsedRecGen(IRecommenderDB db) {
        super(db, REASON);
        sortedCmds = db.getCmdsSortedByUserCount();
    }

    @Override
    public List<String> getRecommendationsForUser(String user, int amount) {
        Set<String> knownCmds = new HashSet<String>();
        knownCmds.addAll(db.getUsedCmdsForUser(user));
        knownCmds.addAll(db.getAlreadyRecommendedCmdsForUser(user));
        return EclipseCmdDevDB.filterOut(sortedCmds, knownCmds, amount);
    }
}
