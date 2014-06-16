package ca.ubc.cs.commandrecommender.data.injector;

import ca.ubc.cs.commandrecommender.db.EclipseCmdDevDB;
import ca.ubc.cs.commandrecommender.db.IRecommenderDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by KeEr on 2014-06-13.
 */
public class UserInjector {
    public static final String DB_NAME = "commands-development";
    public static final String USERS_COLLECTION = "users";
    public static final String LAST_UPLOAD_DATE = "last_upload_date";
    private static final String USER_ID = "user_id";

    public static void main(String args[]) throws UnknownHostException {
        //establish connection
        MongoClient mongoClient = new MongoClient();
        IRecommenderDB recommenderDB = new EclipseCmdDevDB(mongoClient);
        final DBCollection collection = mongoClient.getDB(DB_NAME)
                .getCollection(USERS_COLLECTION);
        for (String user : recommenderDB.getAllUsers()) {
            upsertUser(collection, user);
        }
    }

    private static void upsertUser(DBCollection collection, String user) {
        collection.update(new BasicDBObject(USER_ID, user),
                new BasicDBObject(LAST_UPLOAD_DATE, new Date())
                        .append(USER_ID, user),
                true, false);
    }
}



