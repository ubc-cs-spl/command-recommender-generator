package ca.ubc.cs.commandrecommender.generator;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

/**
 * Created by KeEr on 2014-06-09.
 */
public class RecommendationUpdater {

    public static void main(String[] args) throws UnknownHostException {
        //establish connection
        //TODO: use authorization for production
        MongoClient client = new MongoClient();
        List<String> cmdsSortedByFreq = MostUsedRecGen.getCmdsSortedByFrequency(client);
        for (String user : GeneratorUtils.getAllUsers(client)) {
            if (shouldRecommendToUser(user, client)) {
                MostUsedRecGen.updateRecommendationForUser(user, client, cmdsSortedByFreq, 10);
                updateRecommendationStatus(user, client);
            }
        }
    }

    public static void updateRecommendationStatus(String user, MongoClient client) {
        DBCollection collection = DBUtils.getUsersCollection(client);
        DBObject query = new BasicDBObject(DBUtils.USER_ID, user);
        DBObject update = new BasicDBObject("$set",
                new BasicDBObject(DBUtils.LAST_RECOMMENDATION_DATE, new Date()));
        collection.update(query, update, true, false);
    }

    public static boolean shouldRecommendToUser(String user, MongoClient client) {
        DBCollection collection = DBUtils.getUsersCollection(client);
        DBObject query = new BasicDBObject(DBUtils.USER_ID, user);
        DBObject userEntry = collection.findOne(query);
        Date lastUpload = (Date) userEntry.get(DBUtils.LAST_UPLOAD_DATE);
        Date lastRecommend = (Date) userEntry.get(DBUtils.LAST_RECOMMENDATION_DATE);
        if (lastUpload == null) {
            return false;
        } else if (lastRecommend == null) {
            return true;
        } else {
            return lastUpload.after(lastRecommend);
        }
    }

}
