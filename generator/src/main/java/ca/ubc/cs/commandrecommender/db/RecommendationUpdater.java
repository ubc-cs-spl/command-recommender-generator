package ca.ubc.cs.commandrecommender.db;


import ca.ubc.cs.commandrecommender.generator.AlgorithmType;
import ca.ubc.cs.commandrecommender.generator.IRecGen;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import ca.ubc.cs.commandrecommender.model.User;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

/**
 * Created by KeEr on 2014-06-09.
 */
public class RecommendationUpdater {

    public static void main(String[] args) throws UnknownHostException {
        //establish connection
        //TODO: use authorization for production
        //TODO: close connections properly
        //TODO: make more robust
        String algoName = args[0];
        int amount = Integer.parseInt(args[1]);

        AlgorithmType algoType = AlgorithmType.valueOf(algoName);
        IRecommenderDB db = new EclipseCmdDevDB(new MongoClient());
        db.insureIndex();
        IRecGen recGen = algoType.getRecGen(db);
        String reason = recGen.getAlgorithmUsed();
        for (ToolUseCollection uses : db.getAllData())
            recGen.trainWith(uses);
        recGen.runAlgorithm();
        for (User user : db.getAllUsers()) {
            if (user.isTimeToGenerateRecs()) {
                Iterable<Integer> recommendations = recGen.getRecommendationsForUser(user, amount);
                user.markAllRecommendationOld();
                user.updateRecommendationStatus();
                user.saveRecommendations(recommendations, reason);
            }
        }
    }

}
