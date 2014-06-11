package ca.ubc.cs.commandrecommender.generator;


import com.mongodb.MongoClient;

import java.net.UnknownHostException;

/**
 * Created by KeEr on 2014-06-09.
 */
public class RecommendationUpdater {

    public static void main(String[] args) throws UnknownHostException {
        //establish connection
        //TODO: use authorization for production
        EclipseCmdDevDB db = new EclipseCmdDevDB(new MongoClient());
        AbstractGen algorithm = new MostUsedRecGen(db);
        for (String user : db.getAllUsers()) {
            if (db.shouldRecommendToUser(user)) {
                algorithm.updateRecommendationForUser(user, 10);
                db.updateRecommendationStatus(user);
            }
        }
    }

}
