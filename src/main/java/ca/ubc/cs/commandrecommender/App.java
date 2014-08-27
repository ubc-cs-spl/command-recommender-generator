package ca.ubc.cs.commandrecommender;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.cmdinfo.CommandFunFacts;
import ca.ubc.cs.commandrecommender.cmdinfo.CommandInfoExtractor;
import ca.ubc.cs.commandrecommender.cmdinfo.MongoInfoGenDetailDB;
import ca.ubc.cs.commandrecommender.db.*;
import ca.ubc.cs.commandrecommender.generator.AlgorithmType;
import ca.ubc.cs.commandrecommender.generator.IRecGen;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import ca.ubc.cs.commandrecommender.model.User;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.report.MongoCommandReportDB;
import ca.ubc.cs.commandrecommender.report.MongoReportDB;
import com.mongodb.DBObject;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Main class for running the command line recommendation generator
 */
public class App {
 
    private static AbstractCommandToolConverter toolConverter;   
    private static AbstractCommandDB commandDB;
    private static AbstractRecommendationDB recommendationDB;
    private static IndexMap userIndexMap;
    private static IndexMap toolIndexMap;

    //TODO: should we store these or retrieve from the recommenderOptions?
    private static AlgorithmType algorithmType;
    private static AbstractLearningAcceptance acceptance;
    private static Logger logger = LogManager.getLogger(App.class);
    private static boolean useCache;
    private static RecommenderOptions.GenType genType;
    private static int periodInDays;
    private static int amount;
	private static MongoCommandReportDB commandReportDB;
	private static MongoReportDB reportDB;
	private static RecommenderOptions recommenderOptions;
    private static MongoInfoGenDetailDB infoGenDetailDB;
    private static CommandInfoExtractor commandInfoExtractor;

    public static void main(String[] args) throws DBConnectionException {
        //TODO: use authorization for production
        //TODO: close connections properly
        //TODO: make more robust
        try {
            recommenderOptions = new RecommenderOptions(args);
            useCache = recommenderOptions.isUseCache();
            genType = recommenderOptions.getGenType();
            periodInDays = recommenderOptions.getPeriodInDays();
            amount = recommenderOptions.getAmount();
            algorithmType = recommenderOptions.getAlgorithmType();
            acceptance = recommenderOptions.getAcceptance();            
        } catch (ParseException e) {
           System.out.println(e.getMessage());
           System.exit(1);
        }
        if (genType == RecommenderOptions.GenType.RECOMMENDATION) {
            generateRecommendations();
        } else if (genType == RecommenderOptions.GenType.FUN_FACTS) {
            updateCommandInfo();
        } else {
            generateReports();
        }
    }
    
    private static void generateRecommendations() throws DBConnectionException {
        initializeDatabasesForRecGen();
        long time = System.currentTimeMillis();
        List<ToolUseCollection> toolUses =  commandDB.getAllUsageData();
        logger.debug("Time to Retrieve Data From Database: {}", getAmountOfTimeTaken(time));
        time = System.currentTimeMillis();
        List<User> users = recommendationDB.getAllUsers();
        logger.debug("Retrieved all users in {}", getAmountOfTimeTaken(time));
        if(algorithmType == AlgorithmType.ALL){
        	for(AlgorithmType algoToGenerate : AlgorithmType.values()){
        		if(algoToGenerate != AlgorithmType.ALL){
	        		generateRecommendationsForRecGen(algoToGenerate, toolUses, users);
        		}
        	}
        }else{
            generateRecommendationsForRecGen(algorithmType, toolUses, users);
        }
    }

	private static void generateRecommendationsForRecGen(AlgorithmType algoToGenerate,
                                                         List<ToolUseCollection> toolUses,
                                                         List<User> users) {
        IRecGen recGen = algoToGenerate.getRecGen(acceptance,
                recommendationDB.getNumberOfKnownCommands());
        long time = System.currentTimeMillis();
        for (ToolUseCollection uses : toolUses)
            recGen.trainWith(uses);
        logger.debug("Trained with {}, in {}", toolUses.size(), getAmountOfTimeTaken(time));
        time = System.currentTimeMillis();
        recGen.runAlgorithm();
        logger.debug("Ran Algorithm {} in {}", algoToGenerate.name(), getAmountOfTimeTaken(time));
        int totalUserRecommendation = 0;
        long allUsersTime = System.currentTimeMillis();
        for (User user : users) {
            if ((recommenderOptions.getSpecifiedUser() == null ||
                            user.getUserId().equals(recommenderOptions.getSpecifiedUser())) &&
                    user.isTimeToGenerateRecs()) {
                logger.trace("Generating recommendation process for user: {}", user.getUserId());
                int userId = userIndexMap.getItemByItemId(user.getUserId());
                time = System.currentTimeMillis();
                ToolUseCollection history = commandDB.getUsersUsageData(user.getUserId());
                logger.trace("Retrieving Usage Data for user: {}, number of entries: {}, in {}",
                        user.getUserId(), history.size(), getAmountOfTimeTaken(time));
                time = System.currentTimeMillis();
                RecommendationCollector recommendations =
                        new RecommendationCollector(userId, history.toolsUsedHashSet(), amount);
                recGen.getRecommendationsForUser(recommendations);
                logger.trace("{} Recommendations for user: {}, gathered in {}", recommendations.size(),
                        user.getUserId(), getAmountOfTimeTaken(time));
                time = System.currentTimeMillis();
                user.saveRecommendations(recommendations, algoToGenerate.getRationale(),
                        algoToGenerate.name(), toolIndexMap);
                logger.trace("Saved and completed recommendation gathering process for user: {}, in {}",
                        user.getUserId(), getAmountOfTimeTaken(time));
                totalUserRecommendation++;
            }
        }
        logger.debug("Finished generating recommendations for {} users in {}", totalUserRecommendation,
                getAmountOfTimeTaken(allUsersTime));
	}
    
    private static void generateReports() throws DBConnectionException {
        long startTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(periodInDays);
    	initializeDatabasesForReport();
        long time = System.currentTimeMillis();
        List<String> userIds = reportDB.getRecentlyUploadedUserIds(startTime);
        logger.debug("Time to retrieve users who have recently uploaded: {}", getAmountOfTimeTaken(time));
        time = System.currentTimeMillis();
        List<DBObject> reports = commandReportDB.getUsageReports(startTime, userIds, amount,
                reportDB.getCommandDetailsMap());
        logger.debug("Time to retrieve usage stats from database: {}", getAmountOfTimeTaken(time));
        commandReportDB.closeConnection();
        time = System.currentTimeMillis();
        reportDB.updateCollection(reports);
        logger.debug("Time to record stats: {}", getAmountOfTimeTaken(time));
        reportDB.closeConnection();
    }

    private static void initializeDatabasesForReport() throws DBConnectionException {
        logger.debug("Connecting to Command database with: " +
                recommenderOptions.getCommandConnectionParameters().toString());
        commandReportDB = new MongoCommandReportDB(recommenderOptions);
        logger.debug("Connecting to Recommendation database with: " +
                recommenderOptions.getRecommendationConnectionParamters().toString());
        reportDB = new MongoReportDB(recommenderOptions);
    }

    private static void updateCommandInfo() throws DBConnectionException {
        initializeDatabaseForInfoGen();
        long time = System.currentTimeMillis();
        List<CommandFunFacts> funFactsList = commandInfoExtractor.getCommandInfo();
        logger.debug("Time to extract interesting facts: {}", getAmountOfTimeTaken(time));
        commandInfoExtractor.closeConnection();
        time = System.currentTimeMillis();
        infoGenDetailDB.updateDetails(funFactsList);
        logger.debug("Time to update deatials: {}", getAmountOfTimeTaken(time));
        infoGenDetailDB.closeConnection();
    }

    private static void initializeDatabaseForInfoGen() throws DBConnectionException {
        logger.debug("Connecting to Command database with: " +
                recommenderOptions.getCommandConnectionParameters().toString());
        commandInfoExtractor = new CommandInfoExtractor(recommenderOptions);
        logger.debug("Connecting to Recommendation database with: " +
                recommenderOptions.getRecommendationConnectionParamters().toString());
        infoGenDetailDB = new MongoInfoGenDetailDB(recommenderOptions);
    }

    private static String getAmountOfTimeTaken(long time) {
        long difference = System.currentTimeMillis() - time;
        long second = (difference / 1000) % 60;
        long minute = (difference / (1000 * 60) % 60);
        long hour = (difference / (1000 * 60 * 60) % 60);
        return String.format("%02d:%02d:%02d:%d", hour, minute, second, (difference%1000));
    }

    private static void initializeDatabasesForRecGen() throws DBConnectionException {
        userIndexMap = new IndexMap();
        toolIndexMap = new IndexMap();
        toolConverter = new EclipseCommandToolConverter(toolIndexMap);
        logger.debug("Connecting to Command database with: " +
                recommenderOptions.getCommandConnectionParameters().toString());
        commandDB = new MongoCommandDB(recommenderOptions, toolConverter, userIndexMap, useCache);
        logger.debug("Connecting to Recommendation database with: " +
                recommenderOptions.getRecommendationConnectionParamters().toString());
        recommendationDB = new MongoRecommendationDB(recommenderOptions, userIndexMap);
    }

}
