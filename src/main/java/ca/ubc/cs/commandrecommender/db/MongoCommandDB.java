package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.ToolUse;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Spencer on 6/20/2014.
 */
public class MongoCommandDB extends AbstractCommandDB {
    private MongoClient client;
    private String cmdDbName;
    private DBCollection commandCollection;

    public static final String COMMANDS_COLLECTION = "commands";
    public static final String KIND = "kind";
    public static final String COMMAND = "command";

    public MongoCommandDB(ConnectionParameters connectionParameters, AbstractCommandToolConverter toolConverter, IndexMap userIndexMap) throws DBConnectionException{
        super(toolConverter, userIndexMap);
        this.cmdDbName = connectionParameters.getdBName();
        try {
            client = new MongoClient(connectionParameters.getDbUrl(), connectionParameters.getDbPort());
            commandCollection = client.getDB(connectionParameters.getdBName()).getCollection(COMMANDS_COLLECTION);
            ensureIndex();
        }catch(UnknownHostException ex){
            throw new DBConnectionException(ex);
        }

    }

    private void ensureIndex() {
        if(commandCollection != null) {
            commandCollection.createIndex(new BasicDBObject(toolConverter.getUserIdField(), 1).append(KIND, 1));
        }
    }

    @Override
    public List<ToolUseCollection> getAllUsageData() {
        HashMap<Integer, ToolUseCollection> toolUses = new HashMap<Integer, ToolUseCollection>();
        //We might want to adjust this later to recommend bundles
        DBCursor cursor = commandCollection.find(new BasicDBObject(KIND, COMMAND));
        while(cursor.hasNext()){
            DBObject row = cursor.next();
            String userIdString = (String) row.get(toolConverter.getUserIdField());
            Integer userId = userIndexMap.getItemByItemId(userIdString);
            ToolUse toolUse = toolConverter.convertToToolUse(row.toMap());
            if(!toolUses.containsKey(userId)){
                ToolUseCollection newUserToolUse = new ToolUseCollection(userId);
                newUserToolUse.add(toolUse);
                toolUses.put(userId, newUserToolUse);
            }else{
                toolUses.get(userId).add(toolUse);
            }
        }
        return new ArrayList<ToolUseCollection>(toolUses.values());
    }

    @Override
    public ToolUseCollection getUsersUsageData(String userId) {
        DBObject query = new BasicDBObject(toolConverter.getUserIdField(), userId)
                .append(KIND, COMMAND);
        DBCursor userCommandCursor = commandCollection.find(query);
        Integer userIdIndex = userIndexMap.getItemByItemId(userId);
        ToolUseCollection toolUses = new ToolUseCollection(userIdIndex);
        while(userCommandCursor.hasNext()){
            ToolUse toolUse = toolConverter.convertToToolUse(userCommandCursor.next().toMap());
            toolUses.add(toolUse);
        }
        return toolUses;
    }
}
