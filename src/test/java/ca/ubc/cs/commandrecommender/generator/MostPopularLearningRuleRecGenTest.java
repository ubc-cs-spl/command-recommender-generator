package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.acceptance.IncludeAllAcceptance;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by KeEr on 2014-06-27.
 */
public class MostPopularLearningRuleRecGenTest extends AbstractRecGenTest {

    @Test
    public void testExampleInPaper() {
        rec.trainWith(person(true,1,12,12,12,12,1,2,1,3,1,4));
        rec.trainWith(person(true,2,12,12,12,12,1,5,1,5,1,3));
        rec.trainWith(person(true,3,12,12,12,12,5,6,5,6,5,7));
        rec.runAlgorithm();

        RecommendationCollector r1 = new RecommendationCollector(1,
                new HashSet<Integer>(Arrays.asList(1,2,1,3,1,4)));
        rec.fillRecommendations(r1);
        Iterator<Integer> i1 = r1.iterator();
        assertEquals(new Integer(5), i1.next());
        assertEquals(new Integer(6), i1.next());
        assertEquals(new Integer(7), i1.next());
        assertFalse(i1.hasNext());

        RecommendationCollector r2 = new RecommendationCollector(2,
                new HashSet<Integer>(Arrays.asList(1,5,1,5,1,3)));
        rec.fillRecommendations(r2);
        Iterator<Integer> i2 = r2.iterator();
        assertEquals(new Integer(2), i2.next());
        assertEquals(new Integer(4), i2.next());
        assertEquals(new Integer(6), i2.next());
        assertEquals(new Integer(7), i2.next());
        assertFalse(i2.hasNext());

        RecommendationCollector r3 = new RecommendationCollector(3,
                new HashSet<Integer>(Arrays.asList(5,6,5,6,5,7)));
        rec.fillRecommendations(r3);
        Iterator<Integer> i3 = r3.iterator();
        assertEquals(new Integer(1), i3.next());
        assertEquals(new Integer(3), i3.next());
        assertEquals(new Integer(2), i3.next());
        assertEquals(new Integer(4), i3.next());
        assertFalse(i3.hasNext());
    }

    @Override
    protected AbstractRecGen getRec() {
        return new MostPopularLearningRuleRecGen("", new IncludeAllAcceptance());
    }

}
