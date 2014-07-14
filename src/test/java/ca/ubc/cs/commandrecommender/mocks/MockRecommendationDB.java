package ca.ubc.cs.commandrecommender.mocks;

import ca.ubc.cs.commandrecommender.db.AbstractRecommendationDB;
import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.Rationale;
import ca.ubc.cs.commandrecommender.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spencer on 6/23/2014.
 */
public class MockRecommendationDB extends AbstractRecommendationDB {
    public List<String> savedRecommendations;
    public List<String> savedReasons;
    public List<String> savedUserIds;
    public List<Double> savedReasonValues;
    public List<Double> savedAlgorithmValues;
    public List<String> savedAlgorithmTypes;

    public MockRecommendationDB(IndexMap toolIndexMap) {
        super(toolIndexMap);
        savedRecommendations = new ArrayList<String>();
        savedReasons = new ArrayList<String>();
        savedUserIds = new ArrayList<String>();
        savedReasonValues = new ArrayList<Double>();
        savedAlgorithmTypes = new ArrayList<String>();
        savedAlgorithmValues = new ArrayList<Double>();
    }

    @Override
    public void saveRecommendation(String commandId, String userId, String reason, String algorithmType, Rationale rationale) {
        savedRecommendations.add(commandId);
        savedUserIds.add(userId);
        savedReasons.add(reason);
        savedReasonValues.add(rationale.getValueForTypeSpecificReason());
        savedAlgorithmTypes.add(algorithmType);
        savedAlgorithmValues.add(rationale.getDecisionPointValue());
    }

    @Override
    public void markRecommendationsAsOld(String userId) {

    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public void updateRecommendationStatus(String userId) {

    }
}
