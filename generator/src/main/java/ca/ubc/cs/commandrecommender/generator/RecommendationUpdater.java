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
        CmdDevDB db = new CmdDevDB(new MongoClient());
        List<String> cmdsSortedByFreq = MostUsedRecGen.getCmdsSortedByFrequency(db);
        for (String user : GeneratorUtils.getAllUsers(db)) {
            if (shouldRecommendToUser(user, db)) {
                MostUsedRecGen.updateRecommendationForUser(user, db, cmdsSortedByFreq, 10);
                updateRecommendationStatus(user, db);
            }
        }
    }

    public static void updateRecommendationStatus(String user, CmdDevDB db) {
        DBCollection collection = db.getUsersCollection();
        DBObject query = new BasicDBObject(CmdDevDB.USER_ID, user);
        DBObject update = new BasicDBObject("$set",
                new BasicDBObject(CmdDevDB.LAST_RECOMMENDATION_DATE, new Date()));
        collection.update(query, update, true, false);
    }

    public static boolean shouldRecommendToUser(String user, CmdDevDB db) {
        DBCollection collection = db.getUsersCollection();
        DBObject query = new BasicDBObject(CmdDevDB.USER_ID, user);
        DBObject userEntry = collection.findOne(query);
        Date lastUpload = (Date) userEntry.get(CmdDevDB.LAST_UPLOAD_DATE);
        Date lastRecommend = (Date) userEntry.get(CmdDevDB.LAST_RECOMMENDATION_DATE);
        if (lastUpload == null) {
            return false;
        } else if (lastRecommend == null) {
            return true;
        } else {
            return lastUpload.after(lastRecommend);
        }
    }

}
