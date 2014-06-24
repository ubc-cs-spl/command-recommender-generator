package ca.ubc.cs.commandrecommender.db;


import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.generator.AlgorithmType;
import ca.ubc.cs.commandrecommender.generator.IRecGen;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import ca.ubc.cs.commandrecommender.model.User;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.acceptance.LearningAcceptanceType;

import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.acceptance.LearningAcceptanceType;

import com.mongodb.MongoClient;


import java.net.UnknownHostException;

/**
 * Created by KeEr on 2014-06-09.
 */
public class RecommendationUpdater {
    private static AbstractCommandToolConverter toolConverter;
    private static ConnectionParameters connectionParameters;
    private static AbstractCommandDB commandDB;
    private static AbstractRecommendationDB recommendationDB;
    private static IndexMap userIndexMap;
    private static IndexMap toolIndexMap;

    public static void main(String[] args) throws UnknownHostException, DBConnectionException {
        //establish connection
        //TODO: use authorization for production
        //TODO: close connections properly
        //TODO: make more robust
        String algoName = args[0];
        int amount = Integer.parseInt(args[1]);

        AbstractLearningAcceptance acceptance = null;
        if (args.length == 3)
            acceptance = LearningAcceptanceType.valueOf(args[2]).getAcceptance();

        AlgorithmType algoType = AlgorithmType.valueOf(algoName);
        initializeDatabases();
        IRecGen recGen = algoType.getRecGen();
        String reason = recGen.getAlgorithmUsed();
        for (ToolUseCollection uses : commandDB.getAllUsageData())
            recGen.trainWith(uses);

        recGen.runAlgorithm();
        for (User user : recommendationDB.getAllUsers()) {

            if (user.isTimeToGenerateRecs()) {
                Iterable<Integer> recommendations = recGen.getRecommendationsForUser(user, amount, userIndexMap);
                user.saveRecommendations(recommendations, reason, toolIndexMap);
                user.updateRecommendationStatus();
            }
        }
    }

    private static void initializeDatabases() throws DBConnectionException {
        userIndexMap = new IndexMap();
        toolIndexMap = new IndexMap();
        toolConverter = new EclipseCommandToolConverter(toolIndexMap);
        connectionParameters = new ConnectionParameters("localhost","",20171,"","commands");
        commandDB = new MongoCommandDB(connectionParameters, toolConverter, userIndexMap);
        recommendationDB = new MongoRecommendationDB(connectionParameters, toolConverter, userIndexMap);
    }

}
