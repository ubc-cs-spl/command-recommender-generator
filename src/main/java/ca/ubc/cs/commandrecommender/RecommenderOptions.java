package ca.ubc.cs.commandrecommender;

import ca.ubc.cs.commandrecommender.db.ConnectionParameters;
import ca.ubc.cs.commandrecommender.generator.AlgorithmType;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.acceptance.LearningAcceptanceType;
import org.apache.commons.cli.*;

import java.util.Iterator;

public class RecommenderOptions {
	
   	public static final String COMMAND_HOST = "ch";
    public static final String COMMAND_PORT = "cp";
    public static final String COMMAND_DB_NAME = "cn";
    public static final String RECOMMENDATION_HOST = "rh";
    public static final String RECOMMENDATION_PORT = "rp";
    public static final String RECOMMENDATION_DB_NAME = "rn";
    public static final String COMMAND_USER = "cu";
    public static final String COMMAND_PASS = "cpass";
    public static final String RECOMMENDATION_USER = "ru";
    public static final String RECOMMENDATION_PASS = "rpass";
    public static final String AMOUNT = "a";
    public static final String ALGORITHM_TYPE = "t";
    public static final String ACCEPTANCE_TYPE = "l"; //for learning acceptance
    public static final String USE_CACHE = "c";
    public static final String USER = "u";
    public static final String TIME_PERIOD_IN_DAYS = "p";
    public static final String GENERATE_REPORT = "report";
    public static final String COMMAND_TABLE = "command_table";
    public static final String RECOMMENDATION_TABLE = "recommendation_table";
    public static final String USER_TABLE = "user_table";
    public static final String REPORT_TABLE = "report_table";
    public static final String COMMAND_DETAIL_TABLE = "command_detail_table";
    public static final String HELP = "h";
    
    protected ConnectionParameters commandConnectionParameters = null;
    protected ConnectionParameters recommendationConnectionParameters = null;
    
    private String commandTable = "commands";
 	private String commandDetailTable = "command_details";
 	private String recommendationTable = "recommendations";
 	private String userTable = "users";
 	private String reportTable = "reports";
    
    private String dbName = "commands-production";
    private String dbUrl = "localhost";
    private int port = 27017;

    private CommandLine cmd;
    private Options options;
    
	private String algorithmName = "MOST_WIDELY_USED";
    private AlgorithmType algorithmType;
    private AbstractLearningAcceptance acceptance;
    
    private boolean useCache = false;
    private String user = null;
    private boolean generateReport = false;
    private int periodInDays = 7;
    private int amount = -1;
    
 
	
    
    public RecommenderOptions(String[] args) throws ParseException{
    	this.options = createCommandLineOptions();
        CommandLineParser parser = new BasicParser();
        this.cmd = parser.parse(options, args);
        if(cmd.hasOption(HELP)){
        	throw new ParseException(createHelpMessage());
        }
        parseCommandDbParameters();
		parseRecommendationParameters();
		parseGenerationParameters();
		parseTableParameters();
    }
    

	public ConnectionParameters getCommandConnectionParameters(){
    	return commandConnectionParameters;
    }
    
	public ConnectionParameters getRecommendationConnectionParamters(){
       	return recommendationConnectionParameters;
    }
	
    public int getAmount() {
		return amount;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public AlgorithmType getAlgorithmType() {
		return algorithmType;
	}

	public AbstractLearningAcceptance getAcceptance() {
		return acceptance;
	}

	public String getCommandTable() {
		return commandTable;
	}

	public String getCommandDetailTable() {
		return commandDetailTable;
	}

	public String getRecommendationTable() {
		return recommendationTable;
	}

	public String getUserTable() {
		return userTable;
	}

	public String getReportTable() {
		return reportTable;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public boolean isGenerateReport() {
		return generateReport;
	}

	public int getPeriodInDays() {
		return periodInDays;
	}

    public String getSpecifiedUser() {
        return user;
    }

	private Options createCommandLineOptions() {
		options = new Options();
        createCommandOptions();
        createRecommendationOptions();
        createRecommendationGenerationOptions();
        createTableOptions();
        options.addOption(HELP, false, "help");
        return options;
    }

	protected void createCommandOptions() {
		options.addOption(COMMAND_HOST, true, "Specify the host of your command data store. Default: " + dbUrl);
		options.addOption(COMMAND_PORT, true, "Specify the port of your command data store on. Default: " + port);
        options.addOption(COMMAND_DB_NAME, true, "Name for the database which contains your commands. Default: " + dbName);
        options.addOption(COMMAND_USER, true, "User for your command data store. Default: none");
        options.addOption(COMMAND_PASS, true, "Password for the user for the command data store. Default: none");

	}
	
	protected void createRecommendationOptions() {
		options.addOption(RECOMMENDATION_HOST, true, "Specify the host of your recommendation data store. Default: Same as command data store");
        options.addOption(RECOMMENDATION_PORT, true, "Specify the port of your recommendation data store. Default: Same as command data store");
        options.addOption(RECOMMENDATION_DB_NAME, true, "Specify the name of the database that contains your recommendation and user data. Default: Same as command data store");      
        options.addOption(RECOMMENDATION_USER, true, "User for your recommendation data store. Default: none");
        options.addOption(RECOMMENDATION_PASS, true, "Password for the user for the recommendation data store. Default: none");
        options.addOption(USER, true, "Generate recommendations only for the user specified in this option. Default: generate recommendations for all users");
	}

	protected void createRecommendationGenerationOptions() {
		options.addOption(AMOUNT, true, "The maximum number of commands to include in the user report. Default: unlimited" + amount);
        options.addOption(ALGORITHM_TYPE, true, "Type of algorithm you want to use to generate the recommendations. Default: " + algorithmName);
        options.addOption(ACCEPTANCE_TYPE, true, "Acceptance type for the algorithm. Default: INCLUDE_ALL");
        options.addOption(USE_CACHE, false, "Cache all usage data");
        options.addOption(GENERATE_REPORT, false, "Whether to generate report or recommendations. Default: false (generate recommendations)");
        options.addOption(TIME_PERIOD_IN_DAYS, true, "The time period for the usage report in days. Default: 7");
	}
	
	private void createTableOptions(){
        options.addOption(COMMAND_TABLE, true, "Table that is used for command data. Default: " + commandTable);
        options.addOption(COMMAND_DETAIL_TABLE, true, "Specify table that is used to store the command details. Default: " + commandDetailTable);
        options.addOption(RECOMMENDATION_TABLE, true, "Specify table that is used to store the generated recommendations. Default: " + recommendationTable);
        options.addOption(USER_TABLE, true, "Specify table name that is used to store the users. Default: " + userTable);
        options.addOption(REPORT_TABLE, true, "Specify table name that is used to store generated reports. Default " + reportTable);
	}
	
    private void parseCommandDbParameters() throws ParseException {
        if(cmd.hasOption(COMMAND_HOST)){
            dbUrl = cmd.getOptionValue(COMMAND_HOST);
        }
        if(cmd.hasOption(COMMAND_PORT)){
            try {
                port = Integer.parseInt(cmd.getOptionValue(COMMAND_PORT));
            }catch (NumberFormatException ex){
                throw new ParseException("Invalid Argument: Command port number is not valid.");
            }
        }
        if(cmd.hasOption(COMMAND_DB_NAME)){
            dbName = cmd.getOptionValue(COMMAND_DB_NAME);
        }
        commandConnectionParameters = new ConnectionParameters(dbUrl, port, dbName);

        if(cmd.hasOption(COMMAND_USER)){
            commandConnectionParameters.setDbUser(cmd.getOptionValue(COMMAND_USER));
        }

        if(cmd.hasOption(COMMAND_PASS)){
            commandConnectionParameters.setDbPassword(cmd.getOptionValue(COMMAND_PASS));
        }
	}
    
    private void parseRecommendationParameters() throws ParseException {
        if(cmd.hasOption(RECOMMENDATION_HOST)){
            dbUrl = cmd.getOptionValue(RECOMMENDATION_HOST);
        }
        if(cmd.hasOption(RECOMMENDATION_PORT)){
            try {
                port = Integer.parseInt(cmd.getOptionValue(RECOMMENDATION_PORT));
            }catch (NumberFormatException ex){
                throw new ParseException("Invalid Argument:  Recommendation port number is not valid.");
            }
        }
        if(cmd.hasOption(RECOMMENDATION_DB_NAME)){
            dbName = cmd.getOptionValue(RECOMMENDATION_DB_NAME);
        }
        recommendationConnectionParameters = new ConnectionParameters(dbUrl, port, dbName);	
        
        if(cmd.hasOption(RECOMMENDATION_USER)){
            recommendationConnectionParameters.setDbUser(cmd.getOptionValue(RECOMMENDATION_USER));
        }else{
            recommendationConnectionParameters.setDbUser(commandConnectionParameters.getDbUser());
        }

        if(cmd.hasOption(RECOMMENDATION_PASS)){
            recommendationConnectionParameters.setDbPassword(cmd.getOptionValue(RECOMMENDATION_PASS));
        }else{
            recommendationConnectionParameters.setDbPassword(commandConnectionParameters.getDbPassword());
        }
	}
    
	private void parseGenerationParameters() throws ParseException {
		if(cmd.hasOption(GENERATE_REPORT)) {
			generateReport = true;
		
		    if(cmd.hasOption(TIME_PERIOD_IN_DAYS)) {
		        try {
		            periodInDays = Integer.parseInt(cmd.getOptionValue(TIME_PERIOD_IN_DAYS));
		        } catch (NumberFormatException ex) {
		            throw new ParseException("Invalid Argument: The time period for which the reports will be generated is not valid.");
		        }
		    }
		}

        if(cmd.hasOption(USER)) {
            user = cmd.getOptionValue(USER);
        }

        if(cmd.hasOption(AMOUNT)) {
            try {
                amount = Integer.parseInt(cmd.getOptionValue(AMOUNT));
            }catch (NumberFormatException ex){
                throw new ParseException("Invalid Argument: Not a valid amount.");
            }
        }
	
		if(cmd.hasOption(USE_CACHE)){
		    useCache = true;
		}
	
		if(cmd.hasOption(ALGORITHM_TYPE)){
		    try {
		        algorithmType = AlgorithmType.valueOf(cmd.getOptionValue(ALGORITHM_TYPE));
		    }catch (IllegalArgumentException exp){
		        throw new ParseException("Invalid Argument: Invalid algorithm");
		    }
		}else{
		    algorithmType = AlgorithmType.valueOf(algorithmName);
		}
	
		if(cmd.hasOption(ACCEPTANCE_TYPE)){
		    try {
		        acceptance = LearningAcceptanceType.valueOf(cmd.getOptionValue(ACCEPTANCE_TYPE)).getAcceptance();
		}catch (IllegalArgumentException ex){
		    throw new ParseException("Invalid Argument: Invalid acceptance type.");
		    }
		}else{
            if (algorithmType.needsAcceptance()) {
                throw new ParseException("Invalid Argument: Acceptance type must be specified for the selected algorithm");
            }
            acceptance = null;
        }
	}
	
	private void parseTableParameters() {
		if(cmd.hasOption(COMMAND_TABLE)){
			commandTable = cmd.getOptionValue(COMMAND_TABLE);
		}
		
		if(cmd.hasOption(RECOMMENDATION_TABLE)){
			recommendationTable = cmd.getOptionValue(RECOMMENDATION_TABLE);
		}
		
		if(cmd.hasOption(USER_TABLE)){
			userTable = cmd.getOptionValue(USER_TABLE);
		}
		
		if(cmd.hasOption(COMMAND_DETAIL_TABLE)){
			commandDetailTable = cmd.getOptionValue(COMMAND_DETAIL_TABLE);
		}
		
		if(cmd.hasOption(REPORT_TABLE)){
			reportTable = cmd.getOptionValue(REPORT_TABLE);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private String createHelpMessage() {
		String help = "";
		Iterator optionIterator = options.getOptions().iterator();
		while(optionIterator.hasNext()){
			Option option = (Option) optionIterator.next();
			help += option.getOpt() + " -- " + option.getDescription() + "\r\n";
		}
		return help;
	}

}
