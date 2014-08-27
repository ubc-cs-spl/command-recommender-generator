package ca.ubc.cs.commandrecommender.cmdinfo;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.RecommenderOptions;
import ca.ubc.cs.commandrecommender.db.ConnectionParameters;
import ca.ubc.cs.commandrecommender.db.MongoUtils;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.List;

/**
 * For storing interesting facts into command details
 * Created by KeEr on 2014-08-26.
 */
public class MongoInfoGenDetailDB {

    public static final String COMMAND_ID_FIELD = "command_id";
    public static final String USE_COUNT = "use_count";
    public static final String USER_COUNT = "user_count";
    private MongoClient recommendationClient;
    private DBCollection commandDetailsCollection;
    private ConnectionParameters connectionParameters;

    public MongoInfoGenDetailDB(RecommenderOptions options)
            throws DBConnectionException {
        try {
            this.connectionParameters = options.getRecommendationConnectionParamters();
            recommendationClient = MongoUtils.getMongoClientFromParameters(connectionParameters);
            commandDetailsCollection = recommendationClient.getDB(connectionParameters.getdBName())
                    .getCollection(options.getCommandDetailTable());
            ensureIndex();
        }catch(UnknownHostException ex){
            throw new DBConnectionException(ex);
        }
    }

    private void ensureIndex() {
        if (commandDetailsCollection != null) {
            commandDetailsCollection.createIndex(new BasicDBObject(COMMAND_ID_FIELD, 1));
        }
    }

    public void updateDetails(List<CommandFunFacts> funFactsList) {
        BulkWriteOperation operation = commandDetailsCollection.initializeUnorderedBulkOperation();
        for (CommandFunFacts funFacts : funFactsList) {
            DBObject update = new BasicDBObject("$set",
                    new BasicDBObject(USE_COUNT, funFacts.getUseCount())
                            .append(USER_COUNT, funFacts.getUserCount()));
            operation.find(new BasicDBObject(COMMAND_ID_FIELD, funFacts.getCmdId())).updateOne(update);
        }
        if (funFactsList.size() > 0) {
            operation.execute();
        }
    }

    public void closeConnection() {
        recommendationClient.close();
    }

}
