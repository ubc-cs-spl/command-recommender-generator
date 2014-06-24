package ca.ubc.cs.commandrecommender.model;
import ca.ubc.cs.commandrecommender.mocks.MockRecommendationDB;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
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
    private List<Integer> recommendations;

    @Before
    public void setUp(){
        toolIndexMap = new IndexMap();
        mockRecommendaitonDb = new MockRecommendationDB(toolIndexMap);
        willUpdate = new Date(System.currentTimeMillis());
        recommendations = new ArrayList<Integer>();
        toolUses = createToolUses();
        user = new User(USER_ID, willUpdate, toolUses, mockRecommendaitonDb);
    }

    private ToolUseCollection createToolUses() {
        ToolUseCollection toolUseCollection = new ToolUseCollection(1);
        for(int i=0; i < 20; i++){
            recommendations.add(i);
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
        user.saveRecommendations(recommendations, REASON, toolIndexMap);
        for(Integer recommendationToSave : recommendations){
            assertTrue(mockRecommendaitonDb.savedRecommendations
                    .contains(toolIndexMap.getItemByIndex(recommendationToSave)));
        }

        for(String userId : mockRecommendaitonDb.savedUserIds)
            assertEquals(USER_ID, userId);
        for(String reason : mockRecommendaitonDb.savedReasons)
            assertEquals(REASON, reason);
    }

    @Test
    public void testSaveEmptyRecommendationList(){
        recommendations = new ArrayList<Integer>();
        user.saveRecommendations(recommendations, REASON, toolIndexMap);
        assertTrue(mockRecommendaitonDb.savedRecommendations.isEmpty());
        assertTrue(mockRecommendaitonDb.savedReasons.isEmpty());
        assertTrue(mockRecommendaitonDb.savedUserIds.isEmpty());
    }
}
