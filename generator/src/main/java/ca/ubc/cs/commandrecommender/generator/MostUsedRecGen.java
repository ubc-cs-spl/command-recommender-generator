package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.db.EclipseCmdDevDB;
import ca.ubc.cs.commandrecommender.db.IRecommenderDB;

import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class MostUsedRecGen extends AbstractGen {


    public static final String REASON = "Most frequent commands which you are not using.";

    private final List<String> sortedCmds;

    public MostUsedRecGen(IRecommenderDB db) {
        super(db, REASON);
        sortedCmds = db.getCmdsSortedByFrequency();
    }

    @Override
    public List<String> getRecommendationsForUser(String user, int amount) {
        Set<String> knownCmds = new HashSet<String>();
        knownCmds.addAll(db.getUsedCmdsForUser(user));
        knownCmds.addAll(db.getAlreadyRecommendedCmdsForUser(user));
        return EclipseCmdDevDB.filterOut(sortedCmds, knownCmds, amount);
    }

}
