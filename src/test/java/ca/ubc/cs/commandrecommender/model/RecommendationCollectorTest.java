package ca.ubc.cs.commandrecommender.model;

import junit.framework.TestCase;
import org.junit.Test;

public class RecommendationCollectorTest extends TestCase{
	@Test
	public void testRanking(){
		//TODO
	}
/*
    @Test
    //TODO: change the test
	public void testOverflow(){
		RecommendationCollector rc = new RecommendationCollector(1,null);
		
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
		rc.add(15,1.0); 
		rc.add(11,1.0);
		rc.add(20,0.5); 
		rc.add(21,0.5);
		
		Iterator<Integer> iter = rc.iterator();
		
		for(Integer i = 1; i <= 10; i++ ){
			assertEquals(i, iter.next());
		}
		
		assertFalse(iter.hasNext());
	}

    @Test
    //TODO: change the test
	public void testExactFit(){
		RecommendationCollector rc = new RecommendationCollector(1,null);
		
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
		rc.add(20,0.5); 
		rc.add(21,0.5); 
		
		Iterator<Integer> iter = rc.iterator();
		
		for(Integer i = 1; i <= 10; i++ ){
			assertEquals(i, iter.next());
		}
		
		assertFalse(iter.hasNext());
	}
	*/
}
