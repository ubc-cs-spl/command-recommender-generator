package ca.ubc.cs.commandrecommender;

import java.util.Iterator;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ca.ubc.cs.commandrecommender.db.ConnectionParameters;
import ca.ubc.cs.commandrecommender.generator.AlgorithmType;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.acceptance.LearningAcceptanceType;

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
    public static final String ACCEPTANCE_TYPE = "c";
    public static final String USE_CACHE = "u";
    public static final String TIME_PERIOD_IN_DAYS = "p";
    public static final String GENERATE_REPORT = "report";
    public static final String HELP = "h";
    
    private String dbName = "commands-production";
    private String dbUrl = "localhost";
    private int port = 27017;
    private int amount = -1;
    
    @SuppressWarnings("unused")
	private String[] args;
    private CommandLine cmd;
    private Options options;
    
	private String algorithmName = "MOST_WIDELY_USED";
    private AlgorithmType algorithmType;
    private AbstractLearningAcceptance acceptance;
    
    private boolean useCache = false;
    private boolean generateReport = false;
    private int periodInDays = 7;
    
    private ConnectionParameters commandConnectionParameters = null;
    private ConnectionParameters recommendationConnectionParameters = null;
    
    public RecommenderOptions(String[] args) throws ParseException{
    	this.args = args;
    	this.options = createCommandLineOptions();
        CommandLineParser parser = new BasicParser();
        this.cmd = parser.parse(options, args);
        if(cmd.hasOption(HELP)){
        	throw new ParseException(createHelpMessage());
        }
        parseCommandDbParameters();
		parseRecommendationParameters();
		parseGenerationParameters();
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

	public boolean isUseCache() {
		return useCache;
	}

	public boolean isGenerateReport() {
		return generateReport;
	}

	public int getPeriodInDays() {
		return periodInDays;
	}

	private Options createCommandLineOptions() {
		options = new Options();
        createCommandOptions();
        createRecommendationOptions();
        createRecommendationGenerationOptions();
        options.addOption(HELP, false, "help");
        return options;
    }

	protected void createRecommendationGenerationOptions() {
		options.addOption(AMOUNT, true, "The maximum number of commands to include in the user report. Default: unlimited" + amount);
        options.addOption(ALGORITHM_TYPE, true, "Type of algorithm you want to use to generate the recommendations. Default: " + algorithmName);
        options.addOption(ACCEPTANCE_TYPE, true, "Acceptance type for the algorithm. Default: none");
        options.addOption(USE_CACHE, false, "Cache all usage data");
        options.addOption(GENERATE_REPORT, false, "Whether to generate report or recommendations. Default: false (generate recommendations)");
        options.addOption(TIME_PERIOD_IN_DAYS, true, "The time period for the usage report in days. Default: 7");
	}

	protected void createRecommendationOptions() {
		options.addOption(RECOMMENDATION_HOST, true, "Specify the host of your recommendation data store. Default: Same as command data store");
        options.addOption(RECOMMENDATION_PORT, true, "Specify the port of your recommendation data store. Default: Same as command data store");
        options.addOption(RECOMMENDATION_DB_NAME, true, "Specify the name of the database that contains your recommendation and user data. Default: Same as command data store");      
        options.addOption(RECOMMENDATION_USER, true, "User for your recommendation data store. Default: none");
        options.addOption(RECOMMENDATION_PASS, true, "Password for the user for the recommendation data store. Default: none");
	}

	protected void createCommandOptions() {
		options.addOption(COMMAND_HOST, true, "Specify the host of your command data store. Default: " + dbUrl);
		options.addOption(COMMAND_PORT, true, "Specify the port of your command data store on. Default: " + port);
        options.addOption(COMMAND_DB_NAME, true, "Name for the database which contains your commands. Default: " + dbName);
        options.addOption(COMMAND_USER, true, "User for your command data store. Default: none");
        options.addOption(COMMAND_PASS, true, "Password for the user for the command data store. Default: none");
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
	
		    if(cmd.hasOption(AMOUNT)){
			    try {
			        amount = Integer.parseInt(cmd.getOptionValue(AMOUNT));
			    }catch (NumberFormatException ex){
			        throw new ParseException("Invalid Argument: Not a valid amount.");
		        }
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
		        acceptance = LearningAcceptanceType.valueOf(cmd.getOptionValue('c')).getAcceptance();
		}catch (IllegalArgumentException ex){
		    throw new ParseException("Invalid Argument: Invalid acceptance type.");
		    }
		}else{
		    acceptance = null;
		    if (algorithmType.needsAcceptance())
		        throw new ParseException("Invalid Argument: Acceptance type must be specified for the selected algorithm");
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
