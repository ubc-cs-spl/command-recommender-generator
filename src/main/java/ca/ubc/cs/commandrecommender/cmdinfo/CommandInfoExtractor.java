package ca.ubc.cs.commandrecommender.cmdinfo;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.RecommenderOptions;
import ca.ubc.cs.commandrecommender.db.ConnectionParameters;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * For extracting fun facts about each command from the usage data
 * Created by KeEr on 2014-08-26.
 */
public class CommandInfoExtractor {

    public static final String DESCRIPTION_FIELD = "description";
    public static final String COMMAND_ID_FIELD = "command_id";
    public static final String USER_COUNT_FIELD = "userCount";
    public static final String ID_FIELD = "_id";
    public static final String USER_ID_FIELD = "user_id";
    public static final String USE_COUNT_FIELD = "useCount";
    public static final String KIND_FIELD = "kind";
    public static final String COMMAND_FIELD = "command";

    private DBCollection commandCollection;
    private MongoClient client;

    public CommandInfoExtractor(RecommenderOptions recommenderOptions)
            throws DBConnectionException {
        try {
            ConnectionParameters connectionParameters = recommenderOptions.getCommandConnectionParameters();
            client = new MongoClient(connectionParameters.getDbUrl(), connectionParameters.getDbPort());
            commandCollection = client.getDB(connectionParameters.getdBName())
                    .getCollection(recommenderOptions.getCommandTable());
        }catch(UnknownHostException ex){
            throw new DBConnectionException(ex);
        }
    }

    public void closeConnection() {
        client.close();
    }

    public List<CommandFunFacts> getCommandInfo() {
        List<CommandFunFacts> funFactsList = new ArrayList<CommandFunFacts>();
        AggregationOutput output = commandCollection.aggregate(getInfoPipeline());
        for (DBObject object : output.results()) {
            funFactsList.add(new CommandFunFacts((String) object.get(ID_FIELD),
                    (Integer) object.get(USE_COUNT_FIELD),
                    (Integer) object.get(USER_COUNT_FIELD)));
        }
        return funFactsList;
    }

    private List<DBObject> getInfoPipeline() {
        DBObject match = new BasicDBObject("$match", new BasicDBObject(KIND_FIELD,COMMAND_FIELD));
        DBObject project = new BasicDBObject("$project",
                new BasicDBObject(DESCRIPTION_FIELD, 1).append(USER_ID_FIELD, 1));
        DBObject firstGroup = new BasicDBObject("$group",
                new BasicDBObject(ID_FIELD,
                        new BasicDBObject(USER_ID_FIELD, '$' + USER_ID_FIELD)
                                .append(COMMAND_ID_FIELD, '$' + DESCRIPTION_FIELD))
                        .append(USE_COUNT_FIELD, new BasicDBObject("$sum", 1)));
        DBObject secondGroup = new BasicDBObject("$group",
                new BasicDBObject(ID_FIELD, '$' + ID_FIELD + '.' + COMMAND_ID_FIELD)
                        .append(USE_COUNT_FIELD, new BasicDBObject("$sum", '$' + USE_COUNT_FIELD))
                        .append(USER_COUNT_FIELD, new BasicDBObject("$sum", 1)));
        return Arrays.asList(match, project, firstGroup, secondGroup);
    }

    /* the query used above
    db.commands.aggregate([
    {
        "$match" : {
                "kind" : "command"
        }
    },
    {
            "$project" : {
                    "description" : 1,
                    "user_id" : 1
            }
    },
    {
            "$group" : {
                    "_id" : {
                            "user_id" : "$user_id",
                            "command_id" : "$description"
                    },
                    "useCount" : {
                            "$sum" : 1
                    }
            }
    },
    {
            "$group" : {
                    "_id" : "$_id.command_id",
                    "useCount" : {
                            "$sum" : "$useCount"
                    },
                    "userCount" : {
                            "$sum" : 1
                    }
            }
    }])
     */
}
