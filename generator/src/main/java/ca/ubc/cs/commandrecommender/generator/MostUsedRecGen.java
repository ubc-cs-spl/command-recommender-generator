package ca.ubc.cs.commandrecommender.generator;

import java.util.*;

/**
 * Created by KeEr on 2014-06-09.
 */
public class MostUsedRecGen extends AbstractGen {

    private final List<String> sortedCmds;

    public MostUsedRecGen(EclipseCmdDevDB db) {
        super(db);
        sortedCmds = db.getCmdsSortedByFrequency();
    }

    /**
     *update recommendation with the most frequently used command that a person doesn't know
     */
    @Override
    public void updateRecommendationForUser(String user, int amount) {
        Set<String> knownCmds = new HashSet<String>();
        knownCmds.addAll(db.getUsedCmdsForUser(user));
        knownCmds.addAll(db.getAlreadyRecommendedCmdsForUser(user));
        db.markAllRecommendationOld(user);
        for (String recommendation : EclipseCmdDevDB.filterOut(sortedCmds, knownCmds, amount)) {
            db.insertRecommendation(recommendation, EclipseCmdDevDB.FREQUENT_REASON, user);
        }
    }

}
