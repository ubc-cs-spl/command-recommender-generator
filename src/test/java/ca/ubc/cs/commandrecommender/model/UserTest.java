package ca.ubc.cs.commandrecommender.model;
import ca.ubc.cs.commandrecommender.mocks.MockRecommendationDB;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import static org.junit.Assert.*;
/**
 * Created by Spencer on 6/23/2014.
 */
public class UserTest {
    private String USER_ID = "USER_ID";
    private User user;
    private IndexMap toolIndexMap;
    private MockRecommendationDB mockRecommendationDb;
    private Date willUpdate;
    private HashSet<Integer> toolUses;
    private String REASON = "REASON";
    private String ALGORITHM_TYPE = "ALGORITHM_TYPE";
    private double ALGORITHM_VALUE = 1.0;
    private RecommendationCollector recommendations;
    private double REASON_VALUE = 1.0;

    @Before
    public void setUp(){
        recommendations = new RecommendationCollector(1, new ArrayList<Integer>(), new HashSet<Integer>(), 5);
        toolIndexMap = new IndexMap();
        mockRecommendationDb = new MockRecommendationDB(toolIndexMap);
        willUpdate = new Date(System.currentTimeMillis());
        toolUses = createRecs();
        user = new User(USER_ID, willUpdate, willUpdate, toolUses, mockRecommendationDb);

    }

    private HashSet<Integer> createRecs() {
        HashSet<Integer> recs = new HashSet<Integer>();
        for(int i=0; i < 20; i++){
            recs.add(i);
        }
        return recs;
    }

    @Test
    public void testGetUserId(){
        assertEquals(user.getUserId(), USER_ID);
    }

    @Test
    public void testShouldUpdate(){
        assertTrue(user.isTimeToGenerateRecs());
    }

    @Test
    public void testShouldNotUpdateRecommendations(){
        Calendar sixDaysAgo = Calendar.getInstance();
        sixDaysAgo.add(Calendar.DATE, -6);
        Date willNotUpdate = new Date(sixDaysAgo.getTimeInMillis());
        user = new User(USER_ID, willNotUpdate, willNotUpdate, toolUses, mockRecommendationDb);
        assertFalse(user.isTimeToGenerateRecs());
    }

    @Test
    public void testSaveValidListRecommendation(){
        user.saveRecommendations(recommendations, REASON, ALGORITHM_TYPE, toolIndexMap);
        for(Integer recommendationToSave : recommendations){
            assertTrue(mockRecommendationDb.savedRecommendations
                    .contains(toolIndexMap.getItemByIndex(recommendationToSave)));
        }

        for(String userId : mockRecommendationDb.savedUserIds)
            assertEquals(USER_ID, userId);
        for(String reason : mockRecommendationDb.savedReasons)
            assertEquals(REASON, reason);
        for(String algorithmType : mockRecommendationDb.savedAlgorithmTypes)
            assertEquals(ALGORITHM_TYPE, algorithmType);
        for(double algorithmValue : mockRecommendationDb.savedAlgorithmValues)
            assertEquals(algorithmValue, ALGORITHM_VALUE, 0);
        for(double reasonValue : mockRecommendationDb.savedReasonValues)
            assertEquals(reasonValue, Double.toString(REASON_VALUE));
    }

    @Test
    public void testSaveEmptyRecommendationList(){
        recommendations = new RecommendationCollector(1, null, null);
        user.saveRecommendations(recommendations, REASON, ALGORITHM_TYPE, toolIndexMap);
        assertTrue(mockRecommendationDb.savedRecommendations.isEmpty());
        assertTrue(mockRecommendationDb.savedReasons.isEmpty());
        assertTrue(mockRecommendationDb.savedUserIds.isEmpty());
    }
}
