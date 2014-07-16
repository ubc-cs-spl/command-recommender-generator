package ca.ubc.cs.commandrecommender.generator;


import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Created by KeEr on 2014-06-10.
 */
public class HotkeyRecGenTest extends AbstractRecGenTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rec.trainWith(person(false, false, 1, 1, 1, 2, 2, 3, 1, 8));
        rec.trainWith(person(false, false, 2, 2, 5, 2, 4, 3, 8, 8));
        rec.trainWith(person(true,  true,  3, 1, 2, 3, 3, 2, 7, 8));
        rec.trainWith(person(true,  true,  4, 4, 1, 2, 2, 3, 6, 8));
        rec.trainWith(person(true,  false, 5, 1, 5, 2, 2, 3, 6, 1));
    }

    @Test
    public void onlyCmdsThatTheUserNeverUsedHotkeyWithGetRecommended() {
        RecommendationCollector rc = new RecommendationCollector(3, null);
        rec.fillRecommendations(rc);
        assertFalse(rc.containsRec(3));
        assertFalse(rc.containsRec(2));
        assertFalse(rc.containsRec(7));
        assertFalse(rc.containsRec(8));
        assertTrue(rc.containsRec(1));
    }

    @Test
    public void testMostUsedCommandGetRecommendedFirst() {
        RecommendationCollector rc1 = new RecommendationCollector(1, null);
        rec.fillRecommendations(rc1);
        Iterator<Integer> interator = rc1.iterator();
        assertEquals(new Integer(1), interator.next());
        assertEquals(new Integer(2), interator.next());
        assertEquals(new Integer(3), interator.next());
        assertFalse(interator.hasNext());
    }

    @Test
    public void onlyCmdsThatHasHotkeysGetRecommended() {
        RecommendationCollector rc = new RecommendationCollector(2, null);
        rec.fillRecommendations(rc);
        assertFalse(rc.containsRec(4));
        assertFalse(rc.containsRec(8));
        assertTrue(rc.containsRec(2));
        assertTrue(rc.containsRec(3));
        assertTrue(rc.containsRec(5));
    }

    @Override
    protected AbstractRecGen getRec() {
        return new HotkeyRecGen("");
    }
}
