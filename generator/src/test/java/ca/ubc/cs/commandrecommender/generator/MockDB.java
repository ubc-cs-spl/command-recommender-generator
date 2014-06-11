package ca.ubc.cs.commandrecommender.generator;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by KeEr on 2014-06-10.
 */
public class MockDB {

    public static final String U1 = "U1";
    public static final String U2 = "U2";
    public static final String U3 = "U3";
    public static final String SC1 = "SC1";
    public static final String SC2 = "SC2";
    public static final String C1 = "C1";
    public static final String C2 = "C2";
    public static final String C3 = "C3";
    public static final String C4 = "C4";
    private final EclipseCmdDevDB db;

    public MockDB() throws UnknownHostException {
        db = new EclipseCmdDevDB("generator-test", new MongoClient());
        dropDB();
        insertCommand(C1, U1, true);
        insertCommand(C2, U2, false);
        insertCommand(C3, U3, false);
        insertCommand(C4, U3, true);
        insertCommand(C1, U2, true);
        insertCommand(C1, U3, true);
        insertCommand(C2, U3, false);
        insertCommand(C1, U1, true);
        insertDetail(C1, SC1);
        insertDetail(C2, SC2);
        insertDetail(C3, null);
        insertRecommendation(U1, C1);
        insertRecommendation(U2, C2);
        insertRecommendation(U1, C3);
    }

    public EclipseCmdDevDB getCmdDevDB() {
        return db;
    }

    public void insertUser(String user, Date upload, Date recommend) {
        DBObject entry = new BasicDBObject();
        entry.put(EclipseCmdDevDB.USER_ID, user);
        entry.put(EclipseCmdDevDB.LAST_UPLOAD_DATE, upload);
        entry.put(EclipseCmdDevDB.LAST_RECOMMENDATION_DATE, recommend);
        db.getUsersCollection().insert(entry);
    }

    public void insertDetail(String cmdId, String shortcut) {
        DBObject entry = new BasicDBObject();
        entry.put(EclipseCmdDevDB.COMMAND_ID, cmdId);
        entry.put(EclipseCmdDevDB.SHORTCUT, shortcut);
        db.getDetailsCollection().insert(entry);
    }

    public void insertCommand(String cmdId, String user, boolean bindingUsed) {
        DBObject entry = new BasicDBObject();
        entry.put(EclipseCmdDevDB.DESCRIPTION, cmdId);
        entry.put(EclipseCmdDevDB.USER_ID, user);
        entry.put(EclipseCmdDevDB.KIND, EclipseCmdDevDB.COMMAND);
        entry.put(EclipseCmdDevDB.BINDING_USED, bindingUsed);
        db.getCommandsCollection().insert(entry);
    }

    public void insertRecommendation(String user, String cmd) {
        DBObject entry = new BasicDBObject(EclipseCmdDevDB.USER_ID, user)
                .append(EclipseCmdDevDB.COMMAND_ID, cmd)
                .append(EclipseCmdDevDB.NEW, true);
        db.getRecommendationsCollection().insert(entry);
    }

    public void dropDB() {
        db.getDB().dropDatabase();
    }

    public int countNewRecommendations() {
        return db.getRecommendationsCollection()
                .find(new BasicDBObject(EclipseCmdDevDB.NEW, true))
                .size();
    }

}
