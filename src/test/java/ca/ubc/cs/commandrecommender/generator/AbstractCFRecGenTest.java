package ca.ubc.cs.commandrecommender.generator;


import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AbstractCFRecGenTest extends AbstractRecGenTest {

    @Test
	public void testRecsFromSeveralPeople(){
		rec.trainWith(person(1,1,2,3));
		rec.trainWith(person(2,1,2));
		rec.trainWith(person(3,1,2,4));
		rec.trainWith(addDummyPerson());

		rec.runAlgorithm();

		RecommendationCollector rc = new RecommendationCollector(2, null);
		rec.fillRecommendations(rc);
		assertTrue(rc.containsRec(3));
		assertTrue(rc.containsRec(4));
	}

	@Test
	public void testUnlikelyRec(){
		rec.trainWith(person(1,1,2,3,4,5));
		rec.trainWith(person(2,1,6,7,8,9));
		rec.trainWith(person(3,1,10,11,12,13));
		rec.trainWith(addDummyPerson());

		rec.runAlgorithm();

		RecommendationCollector rc = new RecommendationCollector(2, null);
		rec.fillRecommendations(rc);

		assertTrue(rc.containsRec(2));
		assertTrue(rc.containsRec(3));
		assertTrue(rc.containsRec(4));
		assertTrue(rc.containsRec(5));
		assertTrue(rc.containsRec(10));
		assertTrue(rc.containsRec(11));
		assertTrue(rc.containsRec(12));
	}

	@Test
	public void testNoRec(){
		rec.trainWith(person(1,1,2,3));
		rec.trainWith(person(2,4,5,6));
		rec.trainWith(person(3,7,8,9));
		rec.trainWith(addDummyPerson());

		rec.runAlgorithm();

		RecommendationCollector rc = new RecommendationCollector(2, null);
		rec.fillRecommendations(rc);
		assertFalse(rc.iterator().hasNext());
	}

    @Test
	public void testMoreLikeRec(){
		rec.trainWith(person(1,1,2,3));
		rec.trainWith(person(2,1,2,4));
		rec.trainWith(person(3,1,5,6));
		rec.trainWith(addDummyPerson());

		rec.runAlgorithm();

		RecommendationCollector rc = new RecommendationCollector(2, null);
		rec.fillRecommendations(rc);

		assertEquals(new Integer(3),rc.iterator().next());
		assertTrue(rc.containsRec(3));
		assertTrue(rc.containsRec(5));
		assertTrue(rc.containsRec(6));
	}

    @Test
	public void testYesNoRec(){
		rec.trainWith(person(1,1,2,3));
		rec.trainWith(person(2,1,5,6));
		rec.trainWith(person(3,7,8,9));
		rec.trainWith(addDummyPerson());

		rec.runAlgorithm();

		RecommendationCollector rc = new RecommendationCollector(2, null);
		rec.fillRecommendations(rc);

		assertTrue(rc.containsRec(2));
		assertTrue(rc.containsRec(3));
		assertFalse(rc.containsRec(7));
		assertFalse(rc.containsRec(8));
		assertFalse(rc.containsRec(9));
	}
}