package ca.ubc.cs.commandrecommender.data.injector;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KeEr on 2014-06-06.
 */
public class OldUsageDataInjector {

    public static final String DB_NAME = "commands-development";
    public static final String COMMANDS_COLLECTION = "commands";

    private static final String USER_ID = "user_id";
    private static final String WHAT = "what";
    private static final String KIND = "kind";
    private static final String BUNDLE_VERSION = "bundleVersion";
    private static final String TIME = "time";
    private static final String DESCRIPTION = "description";
    private static final String BINDING_USED = "bindingUsed";
    private static final int bulkLimit = 100000;

    public static void main(String args[]) throws IOException {
        String filePath = args[0];
        //establish connection
        MongoClient mongoClient = new MongoClient();
        final DBCollection collection = mongoClient.getDB(DB_NAME)
                .getCollection(COMMANDS_COLLECTION);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath)));
        try {
            ArrayList<DBObject> bulkData = new ArrayList<DBObject>(bulkLimit);
            long time = System.currentTimeMillis();
            while (true) {
                if (bulkData.size() == bulkLimit) {
                    collection.insert(bulkData);
                    bulkData = new ArrayList<DBObject>(bulkLimit);
                    System.out.println(System.currentTimeMillis() - time);
                    time = System.currentTimeMillis();
                }
                String line = reader.readLine();
                if (line == null) {
                    if (!bulkData.isEmpty()) {
                        collection.insert(bulkData);
                    }
                    break;
                }
                addCommand(bulkData, line);
            }
        } finally {
            reader.close();
        }
    }

    private static void addCommand(List<DBObject> bulkData, String line) {
        String[] tokens = CsvUtils.splitLine(line);
        if (tokens == null)
            return;
        Long when;
        try {
            when = Long.valueOf(tokens[6].trim());
        } catch (NumberFormatException e) {
            return;
        }
        if (!"command".equals(tokens[2])) {
            return;
        }
        DBObject entry = new BasicDBObject();
        entry.put(USER_ID, tokens[0]);
        entry.put(WHAT, tokens[1]);
        entry.put(KIND, tokens[2]);
        entry.put(BUNDLE_VERSION, tokens[4]);
        entry.put(DESCRIPTION, tokens[5]);
        entry.put(TIME, when);
        entry.put(BINDING_USED, true);
        bulkData.add(entry);
    }

}
