package ca.ubc.cs.commandrecommender.db;


import ca.ubc.cs.commandrecommender.generator.AbstractGen;
import ca.ubc.cs.commandrecommender.generator.MostUsedRecGen;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

/**
 * Created by KeEr on 2014-06-09.
 */
public class RecommendationUpdater {

    public static void main(String[] args) throws UnknownHostException {
        //establish connection
        //TODO: use authorization for production
        IRecommenderDB db = new EclipseCmdDevDB(new MongoClient());
        AbstractGen algorithm = new MostUsedRecGen(db);
        for (String user : db.getAllUsers()) {
            if (db.shouldRecommendToUser(user)) {
                String reason = algorithm.getAlgorithmUsed();
                db.markAllRecommendationOld(user);
                db.updateRecommendationStatus(user);
                for (String recommendation : algorithm.getRecommendationsForUser(user, 10))
                    db.insertRecommendation(recommendation, reason, user);
            }
        }
    }

}
