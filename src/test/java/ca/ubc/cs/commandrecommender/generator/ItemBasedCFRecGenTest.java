package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaOptions;
import org.junit.Test;

public class ItemBasedCFRecGenTest extends CFRecGenTest {

    @Test
	public void testBasic(){
		rec.trainWith(person(1,1,2,3));
		rec.trainWith(person(2,1,2));
		rec.trainWith(person(3,2,3));
		
		rec.runAlgorithm();
		
		RecommendationCollector rc = new RecommendationCollector(2, null, 1);
		rec.fillRecommendations(rc);
		assertTrue(rc.containsRec(3));
	}

    @Test
	public void testMoreLikeRec2(){
		rec.trainWith(person(1,1,2,3));
		rec.trainWith(person(2,1,4,5));
		rec.trainWith(person(3,1,6,7));
		rec.trainWith(person(4,1,6,8));
		rec.trainWith(addDummyPerson());
		
		rec.runAlgorithm();
		
		RecommendationCollector rc = new RecommendationCollector(2, null, 5);
		rec.fillRecommendations(rc);
		assertEquals(new Integer(6),rc.iterator().next());
		assertTrue(rc.containsRec(2));
		assertTrue(rc.containsRec(3));
		assertTrue(rc.containsRec(7));//TODO: why would 7 and 8 have different numbers?
		assertTrue(rc.containsRec(8));
	}
	
	@Override
	ItemBasedCFRecGen getRec() {
        return new ItemBasedCFRecGen("", new MatejkaOptions(false, true, 1.0));
    }
}
