package ca.ubc.cs.commandrecommender.report;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

/**
 * A c
 * @author KeEr
 *
 */
public class CommandStats {
	
	/**
	 * 
	 * @param cmdDetailObjectId the id of the command
	 * @param useCount the number of time it has been used
	 * @param hotkeyCount the number of time it's invoked through hotkey
	 */
	public static DBObject create(ObjectId cmdDetailObjectId, int useCount, int hotkeyCount) {
		return new BasicDBObject("command_detail_id", cmdDetailObjectId)
		.append("use_count", useCount)
		.append("hotkey_count", hotkeyCount);
	}
	
}
