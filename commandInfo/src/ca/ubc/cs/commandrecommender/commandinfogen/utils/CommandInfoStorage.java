package ca.ubc.cs.commandrecommender.commandinfogen.utils;

import java.net.UnknownHostException;
import java.util.Collection;

import org.eclipse.core.runtime.Platform;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class CommandInfoStorage {

	private static final String DB_NAME = "commands-development";
	private static final String COMMAND_DETAIL_COLLECTION = "command_details";

	private static final String COMMAND_NAME = "command_name";
	private static final String COMMAND_ID = "command_id";
	private static final String DESCRIPTION = "description";
	private static final String SHORTCUT = "shortcut";
	private static final String SHORTCUT_MAC = "shortcut_mac";

	public static void storeAllKnownCommandInfo() {
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
		DBCollection collection = mongoClient.getDB(DB_NAME)
				.getCollection(COMMAND_DETAIL_COLLECTION);
		Collection<String> knownCmds = CommandInfoUtils.getAllCommands();
		for (String knownCmd : knownCmds) {
			insertNewCmdIfNotExist(knownCmd, collection);
		}
		mongoClient.close();
	}

	private static void insertNewCmdIfNotExist(String knownCmd, DBCollection collection) {
		DBObject command = new BasicDBObject(COMMAND_NAME, CommandInfoUtils.getCommandName(knownCmd));
		DBObject info = getShortCut(knownCmd);
		info.put(COMMAND_NAME, CommandInfoUtils.getCommandName(knownCmd));
		info.put(COMMAND_ID, knownCmd);
		info.put(DESCRIPTION, CommandInfoUtils.getCommandDescription(knownCmd));
		collection.update(command, new BasicDBObject("$set", info), true, false);
	}

	private static DBObject getShortCut(String knownCmd) {
		String shortcut = CommandInfoUtils.getKeyBindingFor(knownCmd);
		if (shortcut == null) {
			return new BasicDBObject();
		} else if (Platform.getOS() == Platform.OS_MACOSX) {
			return new BasicDBObject(SHORTCUT_MAC, shortcut);
		} else {
			return new BasicDBObject(SHORTCUT, shortcut);
		}
	}

}
