package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by KeEr on 2014-06-20.
 */
public class LintonTotalRecommendationTest extends AbstractRecGenTest {

    private int[] p1 = {1,1,1,1,1,1,1,1};
    private int[] p2 = {2,6,4,5,5,6,5,5};
    private int[] p3 = {3,1,4,4,2,3,3,5};
    private int[] p4 = {4,3,3,4,1,3,3,3};

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rec.trainWith(person(p1));
        rec.trainWith(person(p2));
        rec.trainWith(person(p3));
        rec.trainWith(person(p4));
    }

    @Test
    public void mostFrequentlyUsedGetRecommendedFirst() {
        RecommendationCollector rc1 = new RecommendationCollector(
                1, Arrays.asList(1,1,1,1,1,1,1), new HashSet<Integer>());
        rec.fillRecommendations(rc1);
        Iterator<Integer> iterator1 = rc1.iterator();
        assertEquals(new Integer(3), iterator1.next());
        assertEquals(new Integer(5), iterator1.next());
        assertEquals(new Integer(4), iterator1.next());
        assertEquals(new Integer(6), iterator1.next());
        assertEquals(new Integer(2), iterator1.next());
        assertFalse(iterator1.hasNext());
    }

    @Test
    public void usedCmdsAreNotRecommended() {
        RecommendationCollector rc2 = new RecommendationCollector(
                2, Arrays.asList(6,4,5,5,6,5,5), new HashSet<Integer>());
        rec.fillRecommendations(rc2);
        Iterator<Integer> iterator2 = rc2.iterator();
        assertEquals(new Integer(1), iterator2.next());
        assertEquals(new Integer(3), iterator2.next());
        assertEquals(new Integer(2), iterator2.next());
        assertFalse(iterator2.hasNext());
    }

    @Override
    protected AbstractRecGen getRec() {
        return new LintonTotalRecGen("");
    }

}
