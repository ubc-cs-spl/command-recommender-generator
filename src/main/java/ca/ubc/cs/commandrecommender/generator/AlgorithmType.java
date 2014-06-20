package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.db.IRecommenderDB;

/**
 * Created by KeEr on 2014-06-19.
 */
public enum AlgorithmType {

    MOST_FREQUENTLY_USED("most frequently used"),
    MOST_WIDELY_USED("most widely used"),
    HOTKEY_NOT_USED("hotkey available but never used");

    private String reason;
    AlgorithmType(String name) {
        this.reason = name;
    }

    private String getName() {
        return reason;
    }

    public IRecGen getRecGen(IRecommenderDB db) {
        switch (this) {
            case MOST_FREQUENTLY_USED:
                return new LintonTotalRecommendation(db, reason);
            case MOST_WIDELY_USED:
                return new LintonUserRecommendation(db, reason);
            case HOTKEY_NOT_USED:
                return new HotkeyRecGen(db, reason);
            default: //We should never reach here unless we forget to update this method
                return new LintonTotalRecommendation(db, reason);
        }
    }

}
