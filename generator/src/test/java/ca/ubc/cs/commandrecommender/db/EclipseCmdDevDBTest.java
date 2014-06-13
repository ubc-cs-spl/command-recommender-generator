package ca.ubc.cs.commandrecommender.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by KeEr on 2014-06-10.
 */
public class EclipseCmdDevDBTest {

    private MockDB mock;
    private EclipseCmdDevDB db;

    @Before
    public void initTestDB() throws UnknownHostException {
        mock = new MockDB();
        db = mock.getCmdDevDB();
    }

    @After
    public void cleanTestDB() {
        mock.dropDB();
    }

    @Test
    public void testGetAllUsers() {
        List<String> users = db.getAllUsers();
        assertEquals(3, users.size());
        assertTrue(users.contains(MockDB.U1));
        assertTrue(users.contains(MockDB.U2));
        assertTrue(users.contains(MockDB.U3));
    }

    @Test
    public void testGetUsedCmdsForUser() {
        assertEquals(4, db.getUsedCmdsForUser(MockDB.U3).size());
        assertEquals(2, db.getUsedCmdsForUser(MockDB.U2).size());
        assertEquals(0, db.getUsedCmdsForUser("foo").size());
    }

    @Test
    public void testGetAlreadyRecommendedCmds() {
        assertEquals(2, db.getAlreadyRecommendedCmdsForUser(MockDB.U1).size());
        assertEquals(1, db.getAlreadyRecommendedCmdsForUser(MockDB.U2).size());
        assertEquals(0, db.getAlreadyRecommendedCmdsForUser(MockDB.U3).size());
    }

    @Test
    public void testFilterOut() {
        Collection<String> recommendedForU1 = db.getAlreadyRecommendedCmdsForUser(MockDB.U1);
        Collection<String> usedForU3 = db.getUsedCmdsForUser(MockDB.U3);
        Collection<String> usedForU1 = db.getUsedCmdsForUser(MockDB.U1);
        assertEquals(2, db.filterOut(usedForU3, recommendedForU1, 3).size());
        assertEquals(0, db.filterOut(usedForU3, recommendedForU1, 0).size());
        assertEquals(3, db.filterOut(usedForU3, usedForU1, 3).size());
        assertEquals(1, db.filterOut(usedForU3, usedForU1, 1).size());
    }

    @Test
    public void testInsertRecommendation() {
        assertEquals(2, db.getAlreadyRecommendedCmdsForUser(MockDB.U1).size());
        db.insertRecommendation("x", "", MockDB.U1);
        assertEquals(3, db.getAlreadyRecommendedCmdsForUser(MockDB.U1).size());
    }

    @Test
    public void testMarkAllRecommendationOld() {
        assertEquals(3, mock.countNewRecommendations());
        db.markAllRecommendationOld(MockDB.U1);
        assertEquals(1, mock.countNewRecommendations());
        db.markAllRecommendationOld(MockDB.U2);
        assertEquals(0, mock.countNewRecommendations());
    }

    @Test
    public void testGetCmdsSortedByFrequency() {
        List<String> sortedCmds = db.getCmdsSortedByFrequency();
        assertEquals(MockDB.C1,sortedCmds.get(0));
        assertEquals(MockDB.C2,sortedCmds.get(1));
    }

    @Test
    public void testCmdsWithShortcuts() {
        assertEquals(2, db.getCmdsWithShortcuts());
    }

    @Test
    public void testGetCmdsWithShortcutUserUse() {
        assertEquals(2, db.getCmdsForWhichUserKnowsShortcut(MockDB.U1));
        assertEquals(1, db.getCmdsForWhichUserKnowsShortcut(MockDB.U2));
        assertEquals(2, db.getCmdsForWhichUserKnowsShortcut(MockDB.U3));
    }

}
