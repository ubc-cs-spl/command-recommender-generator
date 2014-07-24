package ca.ubc.cs.commandrecommender.report;

import ca.ubc.cs.commandrecommender.Exception.DBConnectionException;
import ca.ubc.cs.commandrecommender.db.ConnectionParameters;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoCommandReportDB {

    public static final String DESCRIPTION_FIELD = "description";
    public static final String COMMAND_ID_FIELD = "command_id";
    public static final String ID_FIELD = "_id";
    public static final String BINDING_USED_FIELD = "bindingUsed";
    public static final String USER_ID_FIELD = "user_id";
    public static final String TIME_FIELD = "time";
    public static final String USE_COUNT_FIELD = "useCount";
    public static final String HOTKEY_COUNT_FIELD = "hotkeyCount";
    public static final String KIND_FIELD = "kind";
    public static final String COMMANDS_COLLECTION = "commands";
    public static final String COMMAND_FIELD = "command";
    
    private DBCollection commandCollection;
    private MongoClient client;

    public MongoCommandReportDB(ConnectionParameters connectionParameters) 
    		throws DBConnectionException {
        try {
            client = new MongoClient(connectionParameters.getDbUrl(), connectionParameters.getDbPort());
            commandCollection = client.getDB(connectionParameters.getdBName()).getCollection(COMMANDS_COLLECTION);
        }catch(UnknownHostException ex){
            throw new DBConnectionException(ex);
        }
    }

    public void closeConnection() {
        client.close();
    }
    
    /**
     * 
     * @param startTime the report will be generated based on usage data collected from the startTime to now
     * @param userIds  the users to generate reports for
     * @param cmdLimit maximum number of commands to output for a user. If negative, no limit is set
     * @return
     */
    public List<DBObject> getUsageReports(long startTime, List<String> userIds, int cmdLimit) {
        AggregationOptions options = AggregationOptions.builder()
                .allowDiskUse(true)
                .outputMode(AggregationOptions.OutputMode.CURSOR)
                .build();
        Cursor rawStats = commandCollection.aggregate(getReportPipeline(startTime, userIds), options);
        PeekingIterator<DBObject> cursor = Iterators.peekingIterator(rawStats);
        List<DBObject> reports = new ArrayList<DBObject>();
        while (cursor.hasNext()) {
        	// retrieve the report for a user
            String userId = (String) ((DBObject) cursor.peek().get(ID_FIELD)).get(USER_ID_FIELD);
            int totalCommandUsed = 0;
            int totalInvocation = 0;
            BasicDBList cmdStats = new BasicDBList(); 
        	while (cursor.hasNext() && 
        			userId.equals(((DBObject) cursor.peek().get(ID_FIELD)).get(USER_ID_FIELD))) {
        		// retrieve the stats for a command for the user
        		DBObject stat = cursor.next();
                int useCount = (Integer) stat.get(USE_COUNT_FIELD);
                if (cmdLimit < 0 || totalCommandUsed < cmdLimit) {
                    String cmdId = (String) ((DBObject) stat.get(ID_FIELD)).get(COMMAND_ID_FIELD);
                    int hotkeyCount = (Integer) stat.get(HOTKEY_COUNT_FIELD);
                	cmdStats.add(CommandStats.create(cmdId, useCount, hotkeyCount));
                }
                totalInvocation += useCount;
                totalCommandUsed++;
        	}
        	reports.add(UsageReport.create(userId, cmdStats, totalInvocation, totalCommandUsed));
        }
        return reports;
    }

    private List<DBObject> getReportPipeline(long startTime, List<String> userIds) {
        DBObject match = new BasicDBObject("$match",
                new BasicDBObject(KIND_FIELD, COMMAND_FIELD)
        				.append(USER_ID_FIELD, new BasicDBObject("$in", userIds))
                        .append(TIME_FIELD, new BasicDBObject("$gt", startTime)));
        DBObject project = new BasicDBObject("$project",
                new BasicDBObject(DESCRIPTION_FIELD, 1)
                        .append(USER_ID_FIELD, 1)
                        .append(BINDING_USED_FIELD, 1));
        DBObject fieldsToGroupBy = new BasicDBObject(USER_ID_FIELD, "$" + USER_ID_FIELD)
                .append(COMMAND_ID_FIELD, "$" + DESCRIPTION_FIELD);
        DBObject groupFields = new BasicDBObject(ID_FIELD, fieldsToGroupBy)
                .append(USE_COUNT_FIELD, new BasicDBObject("$sum", 1))
                .append(HOTKEY_COUNT_FIELD, new BasicDBObject("$sum",
                        new BasicDBObject("$cond", new Object[]{"$"+BINDING_USED_FIELD, 1, 0})));
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort",
                new BasicDBObject(ID_FIELD + "." + USER_ID_FIELD, 1)
                        .append(USE_COUNT_FIELD, -1));
        return Arrays.asList(match, project, group, sort);
    }

    /* The javascript query used
    db.commands.aggregate(
    [
    {
        $match:
        {
            kind:"command",
            time: {
                $gt:<date>
            },
            user: {
                $in:<list of users>
            }
        }
    },
    {
        $project:
        {
            description:1,
            user_id:1,
            bindingUsed:1
        }
    },
    {
        $group:
        {
            _id:
            {
                user_id:"$user_id",
                command_id:"$description"
            },
            useCount:
            {
                $sum:1
            },
            withHotkeyCount:
            {
                $sum:
                {
                    $cond: ["$bindingUsed", 1, 0]
                }
            }
        }
    },
    {
        $sort:
        {
            "_id.user_id": 1,
            useCount: -1
        }
    }
    ],
    {
        allowDiskUse:true
    }
    )
    */

}
