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
public class LintonUserRecommendationTest extends AbstractRecGenTest {

    private int[] p1 = {1,1,1,1,1,1,2};
    private int[] p2 = {2,1,2,3,4,5,5};
    private int[] p3 = {3,1,2,3,1,1,6};
    private int[] p4 = {4,1,2,3,4,5,5};
    private int[] p5 = {5,1,2,3,4};
    private int[] p6 = {6,1,1,1,1,1,1};

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rec.trainWith(person(p1));
        rec.trainWith(person(p2));
        rec.trainWith(person(p3));
        rec.trainWith(person(p4));
        rec.trainWith(person(p5));
        rec.trainWith(person(p6));
    }

    @Test
    public void mostWidelyUsedGetRecommendedFirst() {
        RecommendationCollector rc1 = new RecommendationCollector(1,
                new HashSet<Integer>(Arrays.asList(1,1,1,1,1,2)));
        rec.fillRecommendations(rc1);
        Iterator<Integer> iterator1 = rc1.iterator();
        assertEquals(new Integer(3), iterator1.next());
        assertEquals(new Integer(4), iterator1.next());
        assertEquals(new Integer(5), iterator1.next());
        assertEquals(new Integer(6), iterator1.next());
        assertFalse(iterator1.hasNext());
    }

    @Test
    public void usedCmdsAreNotRecommended() {
        RecommendationCollector rc3 = new RecommendationCollector(3,
                new HashSet<Integer>(Arrays.asList(1,2,3,1,1,6)));
        rec.fillRecommendations(rc3);
        Iterator<Integer> iterator2 = rc3.iterator();
        assertEquals(new Integer(4), iterator2.next());
        assertEquals(new Integer(5), iterator2.next());
        assertFalse(iterator2.hasNext());
    }

    @Override
    protected AbstractRecGen getRec() {
        return new LintonUserRecGen("");
    }

}
