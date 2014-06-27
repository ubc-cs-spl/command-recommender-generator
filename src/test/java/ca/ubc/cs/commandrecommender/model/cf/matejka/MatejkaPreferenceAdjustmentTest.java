package ca.ubc.cs.commandrecommender.model.cf.matejka;

import ca.ubc.cs.commandrecommender.model.cf.BasicUsageModel;
import ca.ubc.cs.commandrecommender.model.cf.ToolUsePreference;
import junit.framework.TestCase;
import org.junit.Test;

public class MatejkaPreferenceAdjustmentTest extends TestCase {

    //The small difference between the expected value and the result is likely
    //caused by the use of Float instead of doubles in MatejkaPreferenceAdjustment
    private static final double DELTA = 0.00000001;

	private BasicUsageModel stub;
	
	public void setUp(){
		stub = new BasicUsageModel();
	}

    @Test
	public void testAdjustment11(){
		
		ToolUsePreference use = stub.makeUseOf(1, (long)1, (long)1);
		
		recompute();		
		
		assertEquals(0.0,use.getValue(),DELTA);
	}

    @Test
	public void testAdjustment12(){
		
		ToolUsePreference use1 = stub.makeUseOf(1, (long)1, (long)1);
		ToolUsePreference use2 = stub.makeUseOf(1, (long)1, (long)2);		
		
		recompute();
		
		assertEquals(0.0,use1.getValue(),DELTA);
		assertEquals(0.0,use2.getValue(),DELTA);
	}

    @Test
	public void testAdjustment22a(){

		ToolUsePreference use1 = stub.makeUseOf(1, (long)1, (long)1);
		ToolUsePreference use2 = stub.makeUseOf(1, (long)2, (long)2);		
		
		recompute();
		
		double expected = alpha()*Math.log(2);
		assertEquals(expected,use1.getValue(),DELTA);
		assertEquals(expected,use2.getValue(),DELTA);
	}

    @Test
	public void testAdjustment22b(){

		ToolUsePreference use1 = stub.makeUseOf(1, (long)1, (long)1);
		ToolUsePreference use2 = stub.makeUseOf(1, (long)2, (long)2);	
		ToolUsePreference use3 = stub.makeUseOf(1, (long)2, (long)1);	
		
		recompute();
		
		assertEquals(0.0,use1.getValue(),DELTA);
		assertEquals(alpha()*0.5*Math.log(2),use2.getValue(),DELTA);
		assertEquals(0.0,use3.getValue(),DELTA);
	}

    @Test
	public void testAdjustment22c(){

		stub.makeUseOf(1, (long)1, (long)2);
		stub.makeUseOf(1, (long)2, (long)2);
		ToolUsePreference use = stub.makeUseOf(3, (long)2, (long)1);		
		
		recompute();
		
		assertEquals(alpha()*0.75*Math.log(2),use.getValue(),DELTA);
	}
	
	//still need to test mutation... seems to screw stuff up!

	
	private double alpha() {
		return 1;
	}

	private void recompute() {
		
		stub.done();
		new MatejkaPreferenceAdjustment(stub).recomputePreferences();		
	}
}