package ca.ubc.cs.commandrecommender.report;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.db.ConnectionParameters;
import ca.ubc.cs.commandrecommender.db.MongoRecommendationDB;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoReportDB extends MongoRecommendationDB {
	
	private DBCollection reportCollection;
	
	public static final String REPORTS_COLLECTION = "reports";
	
	public MongoReportDB(ConnectionParameters connectionParameters) throws DBConnectionException {
		// the index map is useless here, but using null could lead to NPE on super class methods
		super(connectionParameters, new IndexMap());
		reportCollection = getCollection(REPORTS_COLLECTION);
	}
	
	public void updateCollection(List<DBObject> reports) {
		reportCollection.drop();
		reportCollection.insert(reports);
	}
	
	public List<String> getRecentlyUploadedUserIds(long startTime) {
        Date startDate = new Date(startTime);
        DBCursor users = userCollection.find(new BasicDBObject(LAST_UPLOADED_DATE_FIELD,
                new BasicDBObject("$gt", startDate)));
		List<String> result = new ArrayList<String>();
		for (DBObject user : users) {
			result.add((String) user.get(USER_ID_FIELD));
		}
		return result;
	}
	
}
