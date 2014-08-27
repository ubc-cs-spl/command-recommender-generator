package ca.ubc.cs.commandrecommender.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * Created by KeEr on 2014-08-26.
 */
public class MongoUtils {

    public static List<MongoCredential> createCredentialList(ConnectionParameters connectionParameters) {
        MongoCredential userCredential = MongoCredential.createMongoCRCredential(
                connectionParameters.getDbUser(),
                connectionParameters.getdBName(),
                connectionParameters.getDbPassword().toCharArray());
        return Collections.singletonList(userCredential);
    }

    public static MongoClient getMongoClientFromParameters(ConnectionParameters connectionParameters)
            throws UnknownHostException {
        ServerAddress serverAddress = new ServerAddress(connectionParameters.getDbUrl(),
                connectionParameters.getDbPort());
        if(!connectionParameters.getDbUser().equals("")){
            List<MongoCredential> credentialList = MongoUtils.createCredentialList(connectionParameters);
            return new MongoClient(serverAddress, credentialList);
        }else{
            return new MongoClient(serverAddress);
        }
    }

}
