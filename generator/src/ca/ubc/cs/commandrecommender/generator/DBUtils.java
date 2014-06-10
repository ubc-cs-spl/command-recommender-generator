package ca.ubc.cs.commandrecommender.generator;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * Created by KeEr on 2014-06-09.
 */
public class DBUtils {
    public static final String DB_NAME = "commands-development";
    public static final String COMMANDS_COLLECTION = "commands";
    public static final String RECOMMENDATIONS_COLLECTION = "recommendations";
    public static final String USERS_COLLECTION = "users";
    public static final String LAST_RECOMMENDATION_DATE = "last_recommendation_date";
    public static final String LAST_UPLOAD_DATE = "last_upload_date";
    public static final String USER_ID = "user_id";
    public static final String KIND = "kind";
    public static final String COMMAND = "command";
    public static final String COMMAND_ID = "command_id";
    public static final String NEW = "new_recommendation";
    public static final String REASON = "reason";
    public static final String CREATED_ON = "created_on";
    public static final String DESCRIPTION = "description";
    public static final String ID = "_id";
    public static final String BINDING_USED = "bindingUsed";
    public static final String USEFUL = "useful";
    public static final String FREQUENT_REASON = "Most frequent commands which you are not using.";
    public static final String HOTKEY_REASON = "You have never used hot-key to trigger this command.";

    public static DBCollection getCommandsCollection(MongoClient client) {
        return getCollection(client, COMMANDS_COLLECTION);
    }

    public static DBCollection getRecommendationsCollection(MongoClient client) {
        return getCollection(client, RECOMMENDATIONS_COLLECTION);
    }

    public static DBCollection getUsersCollection(MongoClient client) {
        return getCollection(client, USERS_COLLECTION);
    }

    public static DBCollection getCollection(MongoClient client, String collection) {
        return client.getDB(DB_NAME).getCollection(collection);
    }

}
