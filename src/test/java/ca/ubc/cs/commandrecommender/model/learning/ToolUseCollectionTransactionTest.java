package ca.ubc.cs.commandrecommender.model.learning;

import ca.ubc.cs.commandrecommender.generator.AbstractRecGen;
import ca.ubc.cs.commandrecommender.generator.AbstractRecGenTest;
import ca.ubc.cs.commandrecommender.model.ToolUse;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ToolUseCollectionTransactionTest extends AbstractRecGenTest {

    @Test
	public void test1Transaction(){
		ToolUseCollection tu = new ToolUseCollection();
		tu.add(new ToolUse(now(), 1, true));
		tu.add(new ToolUse(now(), 2, true));
		tu.add(new ToolUse(now(), 3, true));
		
		List<Transaction> ts = tu.divideIntoTransactions();
		
		assertEquals(1,ts.size());
	}

    @Test
	public void test2Transaction(){
		ToolUseCollection tu = new ToolUseCollection();
		tu.add(new ToolUse(now(), 1, true));
		tu.add(new ToolUse(now(), 2, true));
		tu.add(new ToolUse(new Timestamp(now().getTime()+100000000), 3, true));
		
		List<Transaction> ts = tu.divideIntoTransactions();
		
		assertEquals(2,ts.size());
	}

    @Test
	public void test3Transaction(){
		ToolUseCollection tu = new ToolUseCollection();
		tu.add(new ToolUse(now(), 1, true));
		tu.add(new ToolUse(now(), 2, true));
		tu.add(new ToolUse(new Timestamp(now().getTime()+100000000), 3, true));
		tu.add(new ToolUse(new Timestamp(now().getTime()+200000000), 4, true));
		
		List<Transaction> ts = tu.divideIntoTransactions();
		
		assertEquals(3,ts.size());
	}	

    @Test
	public void testTransactionSizes(){
		ToolUseCollection tu = new ToolUseCollection();
		tu.add(new ToolUse(now(), 1, true));
		tu.add(new ToolUse(new Timestamp(now().getTime()+100000000), 3, true));
		tu.add(new ToolUse(new Timestamp(now().getTime()+100000000), 4, true));
		tu.add(new ToolUse(new Timestamp(now().getTime()+200000000), 5, true));
		tu.add(new ToolUse(new Timestamp(now().getTime()+200000001), 6, true));
		tu.add(new ToolUse(new Timestamp(now().getTime()+200000002), 7, true));
		
		List<Transaction> ts = tu.divideIntoTransactions();
		
		assertEquals(1,ts.get(0).toolsUsedCount());
		assertEquals(2,ts.get(1).toolsUsedCount());
		assertEquals(3,ts.get(2).toolsUsedCount());
	}

	@Override
    protected AbstractRecGen getRec() {
		return null;
	}

}
