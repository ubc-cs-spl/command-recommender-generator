package ca.ubc.cs.commandrecommender.report;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.db.ConnectionParameters;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.List;

public class MongoReportDB {
	
	private MongoClient client;
	private DBCollection reportCollection;
	
	public static final String REPORTS_COLLECTION = "reports";
	
	public MongoReportDB(ConnectionParameters connectionParameters) throws DBConnectionException {
		try {
			client = new MongoClient(connectionParameters.getDbUrl(),connectionParameters.getDbPort());
			reportCollection = client.getDB(connectionParameters.getdBName()).getCollection(REPORTS_COLLECTION);
		} catch (UnknownHostException e) {
			throw new DBConnectionException(e);
		}
	}
	
	public void updateCollection(List<DBObject> reports) {
		reportCollection.drop();
		reportCollection.insert(reports);
	}
	
	public void closeConnection() {
		client.close();
	}
	
}
