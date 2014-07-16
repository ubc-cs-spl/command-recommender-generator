package ca.ubc.cs.commandrecommender.db;

import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Date;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.Rationale;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * Created by Spencer on 6/23/2014.
 */
public class MongoRecommendationDBTest {
    //TODO: update and add tests
    private MongoClient client;
    private DBCollection userCollection;
    private DBCollection recommendationCollection;
    private DBCollection commandDetailsCollection;
    private String DB_URL = "localhost";
    private int DB_PORT = 27000;
    private String DB_NAME = "commands-test";
    private AbstractRecommendationDB recommendationDB;
    private IndexMap userIndexMap, toolIndexMap;
    private String COMMAND_ID1 = "NEW_COMMAND1";
    private String COMMAND_ID2 = "NEW_COMMAND2";
    private String USER_ID1 = "NEW_USER1";
    private String USER_ID = "NEW_USER";
    private String REASON1 = "REASON1";
    private String ALGORITHM_TYPE1 = "ALGOTYPE1";
    private double ALGORITHM_VALUE1 = 0.28f;
    private double REASON_VALUE1 = 0.3;
    private ObjectId command_detail_object_id_1;
    private Date willUpdate;

    @Before
    public void setUp() throws UnknownHostException, DBConnectionException {
        client = new MongoClient(DB_URL, DB_PORT);
        this.userCollection = getCollection(MongoRecommendationDB.USER_COLLECTION);
        this.recommendationCollection = getCollection(MongoRecommendationDB.USER_RECOMMENDATION_COLLECTION);
        this.commandDetailsCollection = getCollection(MongoRecommendationDB.COMMAND_DETAILS_COLLECTION);
        userIndexMap = new IndexMap();
        toolIndexMap = new IndexMap();
        ConnectionParameters connectionParameters = new ConnectionParameters(DB_URL, DB_PORT, DB_NAME, "", "");
        initializeDataBase();
        recommendationDB = new MongoRecommendationDB(connectionParameters, userIndexMap);
    }

    private void initializeDataBase() {
        BasicDBObject commandDetail1 = new BasicDBObject(MongoRecommendationDB.COMMAND_ID_FIELD, COMMAND_ID1);
        BasicDBObject commandDetail2 = new BasicDBObject(MongoRecommendationDB.COMMAND_ID_FIELD, COMMAND_ID2);
        commandDetailsCollection.insert(commandDetail1);
        commandDetailsCollection.insert(commandDetail2);
        command_detail_object_id_1 = (ObjectId)commandDetail1.get("_id");
    }

    @After
    public void tearDown(){
        userCollection.remove(new BasicDBObject());
        recommendationCollection.remove(new BasicDBObject());
        commandDetailsCollection.remove(new BasicDBObject());
        client.close();
    }

    private DBCollection getCollection(String collection) {
        return client.getDB(DB_NAME).getCollection(collection);
    }

    private void saveRecommendation(String cmdId, String userId) {
        Rationale rationale = new Rationale(ALGORITHM_VALUE1);
        rationale.setValueForTypeSpecificReason(REASON_VALUE1);
        recommendationDB.saveRecommendation(cmdId, userId, REASON1, ALGORITHM_TYPE1, rationale);
    }

    @Test
    public void testSaveRecommendationValid(){
        saveRecommendation(COMMAND_ID1, USER_ID1);
        DBObject recommendationQuery = new BasicDBObject(MongoRecommendationDB.USER_ID_FIELD, USER_ID1);
        DBCursor cursor = recommendationCollection.find(recommendationQuery);
        int numRecommendationsFound = 0;
        while(cursor.hasNext()){
            DBObject recommendation = cursor.next();
            assertEquals(command_detail_object_id_1, recommendation.get(MongoRecommendationDB.COMMAND_DETAIL_ID_FIELD));
            assertEquals(USER_ID1, recommendation.get(MongoRecommendationDB.USER_ID_FIELD));
            assertEquals(REASON1, recommendation.get(MongoRecommendationDB.REASON_FIELD));
            assertEquals(ALGORITHM_TYPE1, recommendation.get(MongoRecommendationDB.ALGORITHM_TYPE_FIELD));
            assertEquals(ALGORITHM_VALUE1, (Double) recommendation.get(MongoRecommendationDB.ALGORITHM_VALUE_FIELD), 0.0);
            numRecommendationsFound++;
        }
        assertEquals(1, numRecommendationsFound);
    }

    @Test
    public void testSaveRecommendationCommandIdNull(){
        saveRecommendation(null, USER_ID1);
        DBObject recommendationQuery = new BasicDBObject(MongoRecommendationDB.USER_ID_FIELD, USER_ID1);
        DBCursor cursor = recommendationCollection.find(recommendationQuery);
        int numRecommendationsFound = 0;
        while(cursor.hasNext()){
            cursor.next();
            numRecommendationsFound++;
        }
        assertEquals(0, numRecommendationsFound);
    }

    @Test
    public void testSaveRecommendationUserIdEmpty(){
        saveRecommendation(COMMAND_ID1, "");
        DBObject recommendationQuery = new BasicDBObject(MongoRecommendationDB.USER_ID_FIELD, USER_ID1);
        DBCursor cursor = recommendationCollection.find(recommendationQuery);
        int numRecommendationsFound = 0;
        while(cursor.hasNext()){
            cursor.next();
            numRecommendationsFound++;
        }
        assertEquals(0, numRecommendationsFound);
    }
    
    @Test
    public void testSaveRecommendationEnsureUniqueness() {
    	saveRecommendation(COMMAND_ID1, USER_ID1);
    	saveRecommendation(COMMAND_ID2, USER_ID1);
    	saveRecommendation(COMMAND_ID1, USER_ID1); //duplicates will not be added
    	saveRecommendation("adf", "asdf");         //unknow cmd will not be added
    	//duplicates will not be added
    	recommendationDB.saveRecommendation(COMMAND_ID2, USER_ID1, "a", ALGORITHM_TYPE1, new Rationale(0));
    	assertEquals(2, recommendationCollection.count());
    }

    @Test
    public void testGetAllUsers(){
    	insertNewUsers(5);
        assertEquals(5, recommendationDB.getAllUsers().size());
    }
    
    private void insertNewUsers(int amount) {
        for(int i=0; i < amount; i++){
            String userId =  USER_ID + i;
            BasicDBObject new_user = new BasicDBObject()
                    .append(MongoRecommendationDB.USER_ID_FIELD, userId)
                    .append(MongoRecommendationDB.LAST_UPLOADED_DATE_FIELD, willUpdate);
            userCollection.insert(new_user);
        }
    }
    
}
