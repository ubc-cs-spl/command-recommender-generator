package ca.ubc.cs.commandrecommender;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.db.*;
import ca.ubc.cs.commandrecommender.generator.AlgorithmType;
import ca.ubc.cs.commandrecommender.generator.IRecGen;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import ca.ubc.cs.commandrecommender.model.User;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.acceptance.LearningAcceptanceType;
import ca.ubc.cs.commandrecommender.report.MongoCommandReportDB;
import ca.ubc.cs.commandrecommender.report.MongoReportDB;
import com.mongodb.DBObject;
import org.apache.commons.cli.*;
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

    private static AlgorithmType algorithmType;
    private static AbstractLearningAcceptance acceptance;
    private static Logger logger = LogManager.getLogger(App.class);
    private static boolean useCache;
    private static boolean generateReport;
    private static int periodInDays;
    private static int amount;
	private static MongoCommandReportDB commandReportDB;
	private static MongoReportDB reportDB;
	private static RecommenderOptions recommenderOptions;

    public static void main(String[] args) throws DBConnectionException {
        //TODO: use authorization for production
        //TODO: close connections properly
        //TODO: make more robust
        try {
            recommenderOptions = new RecommenderOptions(args);
            useCache = recommenderOptions.isUseCache();
            generateReport = recommenderOptions.isGenerateReport();
            periodInDays = recommenderOptions.getPeriodInDays();
            amount = recommenderOptions.getAmount();
            algorithmType = recommenderOptions.getAlgorithmType();
            acceptance = recommenderOptions.getAcceptance();            
        } catch (ParseException e) {
           System.out.println(e.getMessage());
           System.exit(1);
        }
        if (generateReport) {
        	generateReports();
        } else {
        	generateRecommendations();
        }
    }
    
    private static void generateRecommendations() throws DBConnectionException {
        initializeDatabasesForRecGen();
        IRecGen recGen = algorithmType.getRecGen(acceptance, recommendationDB.getNumberOfKnownCommands());
        long time = System.currentTimeMillis();
        List<ToolUseCollection> toolUses =  commandDB.getAllUsageData();
        logger.debug("Time to Retrieve Data From Database: {}", getAmountOfTimeTaken(time));
        time = System.currentTimeMillis();
        for (ToolUseCollection uses : toolUses)
            recGen.trainWith(uses);
        logger.debug("Trained with {}, in {}", toolUses.size(), getAmountOfTimeTaken(time));
        time = System.currentTimeMillis();
        recGen.runAlgorithm();
        logger.debug("Ran Algorithm {} in {}", algorithmType.getRationale(), getAmountOfTimeTaken(time));
        int totalUserRecommendation = 0;
        long allUsersTime = System.currentTimeMillis();
        List<User> users = recommendationDB.getAllUsers();
        logger.debug("Retrieved all users in {}", getAmountOfTimeTaken(allUsersTime));
        for (User user : users) {
            if (user.isTimeToGenerateRecs()) {
                logger.trace("Generating recommendation process for user: {}", user.getUserId());
                int userId = userIndexMap.getItemByItemId(user.getUserId());
                time = System.currentTimeMillis();
                ToolUseCollection history = commandDB.getUsersUsageData(user.getUserId());
                logger.trace("Retrieving Usage Data for user: {}, number of entries: {}, in {}",
                        user.getUserId(), history.size(), getAmountOfTimeTaken(time));
                time = System.currentTimeMillis();
                RecommendationCollector recommendations = recGen.getRecommendationsForUser(user, history, amount, userId);
                logger.trace("Recommendations for user: {}, gathered in {}", user.getUserId(), getAmountOfTimeTaken(time));
                time = System.currentTimeMillis();
                user.saveRecommendations(recommendations, algorithmType.getRationale(), algorithmType.name(), toolIndexMap);
                logger.trace("Saved and completed recommendation gathering process for user: {}, in {}",
                        user.getUserId(), getAmountOfTimeTaken(time));
                totalUserRecommendation++;
            }
        }
        logger.debug("Finished generating recommendations for {} users in {}", totalUserRecommendation, getAmountOfTimeTaken(allUsersTime));
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
        logger.debug("Connecting to Command database with: " + recommenderOptions.getCommandConnectionParameters().toString());
        commandDB = new MongoCommandDB(recommenderOptions, toolConverter, userIndexMap, useCache);
        logger.debug("Connecting to Recommendation database with: " + recommenderOptions.getRecommendationConnectionParamters().toString());
        recommendationDB = new MongoRecommendationDB(recommenderOptions, userIndexMap);
    }
    
    private static void initializeDatabasesForReport() throws DBConnectionException {
        logger.debug("Connecting to Command database with: " + recommenderOptions.getCommandConnectionParameters().toString());
        commandReportDB = new MongoCommandReportDB(recommenderOptions);
        logger.debug("Connecting to Recommendation database with: " + recommenderOptions.getRecommendationConnectionParamters().toString());
        reportDB = new MongoReportDB(recommenderOptions);
    }

}
