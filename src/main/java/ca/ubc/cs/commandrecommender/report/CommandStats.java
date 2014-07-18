package ca.ubc.cs.commandrecommender.report;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * A c
 * @author KeEr
 *
 */
public class CommandStats {
	
	/**
	 * 
	 * @param cmdId the id of the command
	 * @param useCount the number of time it has been used
	 * @param hotkeyCount the number of time it's invoked through hotkey
	 */
	public static DBObject create(String cmdId, int useCount, int hotkeyCount) {
		return new BasicDBObject("command_id", cmdId)
		.append("use_count", useCount)
		.append("hotkey_count", hotkeyCount);
	}
	
}
