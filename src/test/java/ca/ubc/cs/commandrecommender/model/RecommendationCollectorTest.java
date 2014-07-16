package ca.ubc.cs.commandrecommender.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

public class RecommendationCollectorTest {
	
	@Test
	public void recommendationsWithEqualValueShouldBeInAscendingOrder() {
		RecommendationCollector rc = new RecommendationCollector(1, null);

		rc.add(1, 5.0); 
		rc.add(2, 4.0); 
		rc.add(3, 4.0); 
		rc.add(5, 3.0); 
		rc.add(4, 3.0); 
		rc.add(6, 2.0); 
		rc.add(9, 1.0); 
		rc.add(8, 1.0); 
		rc.add(7, 1.0); 
		rc.add(10,1.0); 
		rc.add(11,1.0); 
		rc.add(12,1.0);
		rc.add(13,0.5); 
		rc.add(14,0.5);
		
		Iterator<Integer> iter = rc.iterator();
		
		for(Integer i = 1; i <= 14; i++ ){
			assertEquals(i, iter.next());
		}
		assertFalse(iter.hasNext());
	}

    @Test (expected = IllegalArgumentException.class)
	public void errorOccursIfToolNotAddedInDescendingOrderByValue() {
		RecommendationCollector rc = new RecommendationCollector(1, null);
		
		rc.add(1, 5.0); 
		rc.add(2, 4.0); 
		rc.add(3, 7.0);
		rc.add(15,1.0); 
		rc.add(11,1.0);
		rc.add(20,0.5); 
		rc.add(21,0.5);
	}

    @Test
	public void noDuplicatsShouldBeCollected() {
		RecommendationCollector rc = new RecommendationCollector(1, null);
		
		rc.add(1, 5.0); 
		rc.add(2, 4.0); 
		rc.add(3, 4.0); 
		rc.add(5, 3.0); 
		rc.add(4, 3.0); 
		rc.add(6, 2.0); 
		rc.add(9, 1.0); 
		rc.add(8, 1.0); 
		rc.add(7, 1.0); 
		rc.add(1, 1.0); 
		rc.add(2, 0.5); 
		rc.add(3, 0.5); 
		
		Iterator<Integer> iter = rc.iterator();
		
		for(Integer i = 1; i <= 9; i++ ){
			assertEquals(i, iter.next());
		}
		assertFalse(iter.hasNext());
	}
	
    @Test
    public void rankingInfoShouldBeAddedToRationalesCorrectly() {
    	RecommendationCollector rc = new RecommendationCollector(1, null);
		
    	rc.add(2, 5.0); 
		rc.add(1, 4.0); 
		rc.add(3, 4.0); 
		rc.add(5, 3.0); 
		rc.add(4, 3.0); 
		rc.add(9, 2.0); 
		rc.add(6, 1.0); 
		rc.add(8, 1.0); 
		rc.add(7, 1.0); 
		rc.add(10,1.0); 
		rc.add(12,0.5); 
		rc.add(11,0.5); 
		
		Map<Integer, Rationale> map = rc.getRationales();
		assertEquals(1, map.get(2).getRank());
		assertEquals(2, map.get(1).getRank());
		assertEquals(2, map.get(3).getRank());
		assertEquals(4, map.get(4).getRank());
		assertEquals(4, map.get(5).getRank());
		assertEquals(6, map.get(9).getRank());
		assertEquals(7, map.get(6).getRank());
		assertEquals(7, map.get(7).getRank());
		assertEquals(7, map.get(8).getRank());
		assertEquals(7, map.get(10).getRank());
		assertEquals(11, map.get(11).getRank());
		assertEquals(11, map.get(12).getRank());
    }
    
}
