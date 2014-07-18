package ca.ubc.cs.commandrecommender.report;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Generating DBOjbect representing a usage report for a user
 * @author KeEr
 *
 */
public class UsageReport {
	
	/**
	 * 
	 * @param userId the user_id
	 * @param cmdStats the command stats of commands used
	 * @param totalInvokation the total number of command invokation
	 */
	public static DBObject create(String userId, BasicDBList cmdStats, int totalInvokation) {
		return new BasicDBObject("user_id", userId)
                .append("command_stats", cmdStats)
                .append("total_invokation", totalInvokation);
	}

}
