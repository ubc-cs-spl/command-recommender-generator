package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.ToolUse;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import junit.framework.TestCase;
import org.junit.Before;

import java.sql.Timestamp;

public abstract class AbstractRecGenTest extends TestCase {

	protected AbstractRecGen rec;

    @Before
	public void setUp() throws Exception {
		super.setUp();
		rec = getRec();
	}

	public ToolUseCollection person(int... ns) {
		ToolUseCollection person = new ToolUseCollection(ns[0]);
		for(int i = 1; i<ns.length; i++){
			person.add(new ToolUse(now(), ns[i], true));
		}
		return person;
	}

	protected ToolUseCollection addDummyPerson() {
		ToolUseCollection p = new ToolUseCollection(-1);
		p.add(new ToolUse(now(), -1, true));
		return p;
	}

	protected Timestamp now() {
		return new Timestamp(System.currentTimeMillis());
	}

	abstract AbstractRecGen getRec();

}