package ca.ubc.cs.commandrecommender.db;


import ca.ubc.cs.commandrecommender.generator.AbstractRecGen;
import ca.ubc.cs.commandrecommender.generator.MostUsedRecGen;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

/**
 * Created by KeEr on 2014-06-09.
 */
public class RecommendationUpdater {

    public static void main(String[] args) throws UnknownHostException {
        //establish connection
        //TODO: use authorization for production
        //TODO: close connections properly
        EclipseCmdDevDB db = new EclipseCmdDevDB(new MongoClient());

        db.insureIndex();
        AbstractRecGen algorithm = new MostUsedRecGen(db);
        String reason = algorithm.getAlgorithmUsed();

        for (String user : db.getAllUsers()) {
            if (db.shouldRecommendToUser(user)) {
                db.markAllRecommendationOld(user);
                db.updateRecommendationStatus(user);
                for (String recommendation : algorithm.getRecommendationsForUser(user, 10))
                    db.insertRecommendation(recommendation, reason, user);
            }
        }
    }

}
