package ca.ubc.cs.commandrecommender.model;
import ca.ubc.cs.commandrecommender.mocks.MockRecommendationDB;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;
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
    private MockRecommendationDB mockRecommendaitonDb;
    private Date willUpdate;
    private ToolUseCollection toolUses;
    private String REASON = "REASON";
    private String ALGORITHM_TYPE = "ALGORITHM_TYPE";
    private double ALGORITHM_VALUE = 1.0;
    private RecommendationCollector recommendations;
    private double REASON_VALUE = 0.0;

    @Before
    public void setUp(){
        toolIndexMap = new IndexMap();
        mockRecommendaitonDb = new MockRecommendationDB(toolIndexMap);
        willUpdate = new Date(System.currentTimeMillis());
        toolUses = createToolUses();
        user = new User(USER_ID, willUpdate, toolUses, mockRecommendaitonDb);

    }

    private ToolUseCollection createToolUses() {
        recommendations = new RecommendationCollector(1, new ArrayList<Integer>(), new HashSet<Integer>());
        ToolUseCollection toolUseCollection = new ToolUseCollection(1);
        for(int i=0; i < 20; i++){
            recommendations.add(i, REASON_VALUE);
            Integer itemIndex = toolIndexMap.addItem(String.valueOf(i));
            toolUseCollection.add(new ToolUse(new Timestamp(System.currentTimeMillis()), itemIndex, true));
        }
        return toolUseCollection;
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
        user = new User(USER_ID, willNotUpdate, toolUses, mockRecommendaitonDb);
        assertFalse(user.isTimeToGenerateRecs());
    }

    @Test
    public void testSaveValidListRecommendation(){
        user.saveRecommendations(recommendations, REASON, ALGORITHM_TYPE, toolIndexMap);
        for(Integer recommendationToSave : recommendations){
            assertTrue(mockRecommendaitonDb.savedRecommendations
                    .contains(toolIndexMap.getItemByIndex(recommendationToSave)));
        }

        for(String userId : mockRecommendaitonDb.savedUserIds)
            assertEquals(USER_ID, userId);
        for(String reason : mockRecommendaitonDb.savedReasons)
            assertEquals(REASON, reason);
        for(String algorithmType : mockRecommendaitonDb.savedAlgorithmTypes)
            assertEquals(ALGORITHM_TYPE, algorithmType);
        for(double algorithmValue : mockRecommendaitonDb.savedAlgorithmValues)
            assertEquals(algorithmValue, ALGORITHM_VALUE, 1.0);
        for(Double reasonValue : mockRecommendaitonDb.savedReasonValues)
            assertEquals(reasonValue, REASON_VALUE, 0);
    }

    @Test
    public void testSaveEmptyRecommendationList(){
        recommendations = new RecommendationCollector(1, null, null);
        user.saveRecommendations(recommendations, REASON, ALGORITHM_TYPE, toolIndexMap);
        assertTrue(mockRecommendaitonDb.savedRecommendations.isEmpty());
        assertTrue(mockRecommendaitonDb.savedReasons.isEmpty());
        assertTrue(mockRecommendaitonDb.savedUserIds.isEmpty());
    }
}
