package ca.ubc.cs.commandrecommender.data.injector;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

/**
 * Created by KeEr on 2014-06-13.
 */
public class UserInjector {
    public static final String DB_NAME = "commands-development";
    public static final String USERS_COLLECTION = "users";
    public static final String COMMANDS_COLLECTION = "commands";
    public static final String LAST_UPLOAD_DATE = "last_upload_date";
    private static final String USER_ID = "user_id";

    public static void main(String args[]) throws UnknownHostException {
        //establish connection
        MongoClient mongoClient = new MongoClient();
        final DBCollection collection = mongoClient.getDB(DB_NAME)
                .getCollection(USERS_COLLECTION);
        long time = System.currentTimeMillis();
        BulkWriteOperation operation =
                collection.initializeUnorderedBulkOperation();
        boolean hasOperation = false;
        for (String user : getAllUsers(mongoClient)) {
            updateUser(operation, user);
            hasOperation = true;
        }
        if (hasOperation) {
            System.out.println(operation.execute());
        }
        System.out.println(System.currentTimeMillis() - time);
    }

    private static void updateUser(BulkWriteOperation operation, String user) {
        operation.find(new BasicDBObject(USER_ID, user)).upsert()
                .updateOne(new BasicDBObject("$set",
                        new BasicDBObject(LAST_UPLOAD_DATE, new Date())));
    }

    private static List<String> getAllUsers(MongoClient client) {
        // swap USERS_COLLECTION with COMMANDS_COLLECTION to populate users
        DBCollection users = client.getDB(DB_NAME).getCollection(COMMANDS_COLLECTION);
        return users.distinct(USER_ID);
    }

    // some queries for manipulating the users collection for testing
    // and analysis purposes

    // generate a collection of users and number of commands uploaded. Used for filtering out users
    // db.commands.aggregate([{$group:{_id: "$user_id", total: {$sum: 1}}}, {$out: "user_temp"}])
    // example filtering
    // db.users.remove({user_id: {$in: db.user_temp.distinct('_id', {total: {$lt: 500}})}})
    // db.commands.remove({user_id: {$nin: db.users.distinct('user_id')}})
}



