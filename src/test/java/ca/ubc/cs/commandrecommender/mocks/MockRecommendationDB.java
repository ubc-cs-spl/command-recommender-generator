package ca.ubc.cs.commandrecommender.mocks;

import ca.ubc.cs.commandrecommender.db.AbstractRecommendationDB;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Spencer on 6/23/2014.
 */
public class MockRecommendationDB extends AbstractRecommendationDB {
    public List<String> savedRecommendations;
    public List<String> savedReasons;
    public List<String> savedUserIds;

    public MockRecommendationDB(IndexMap toolIndexMap) {
        super(toolIndexMap);
        savedRecommendations = new ArrayList<String>();
        savedReasons = new ArrayList<String>();
        savedUserIds = new ArrayList<String>();
    }

    @Override
    public void saveRecommendation(String commandId, String userId, String reason) {
        savedRecommendations.add(commandId);
        savedUserIds.add(userId);
        savedReasons.add(reason);
    }

    @Override
    public void markRecommendationsAsOld(String userId) {

    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }
}
