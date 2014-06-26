package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.ToolUse;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import org.junit.Before;

import java.sql.Timestamp;

public abstract class AbstractRecGenTest {

	protected AbstractRecGen rec;

    @Before
	public void setUp() throws Exception {
		rec = getRec();
	}

	public ToolUseCollection person(int... ns) {
		return person(false, false, ns);
	}

    public ToolUseCollection person(boolean hotkey, boolean alternate, int... ns) {
        ToolUseCollection person = new ToolUseCollection(ns[0]);
        for(int i = 1; i<ns.length; i++){
            boolean shortcut = alternate ? ((i % 2) == 0) : hotkey;
            person.add(new ToolUse(now(), ns[i], shortcut));
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

	protected abstract AbstractRecGen getRec();

}