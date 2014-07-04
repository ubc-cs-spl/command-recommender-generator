package ca.ubc.cs.commandrecommender.model.cf;

import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaPreferenceAdjustment;

import java.util.HashSet;
import java.util.Set;

/**
 * A base DataModel for pure CF algorithms
 *
 * Performance Note:
 * Everything here should be linear time, with the exception of 
 * getNumUsersWithPreferenceFor, which could be precomputed, 
 * but would produce too-large data structure.  It's no worse
 * than linear. 
 */
public abstract class UsageModel extends GenericUsageModel<ToolUsePreference, ItemFactory, UserFactory> {

	public UsageModel() {
		super(new ProgrammerFactory(),new ToolFactory());
	}

    /**
     * Recomputes preferences value, based on Matejka's paper
     */
	protected void recomputePreference(){
		new MatejkaPreferenceAdjustment(this).recomputePreferences();
	}

    /**
     * Put the information about what user used which tool how many times into
     * @param useCount
     * @param userid
     * @param originid
     * @return the ToolUsePreference corresponding to the info
     */
	protected ToolUsePreference makeUseOf(int useCount, long userid, long originid){
		//originid represents toolid
		((ToolFactory)itemFactory).getOrCreateToolForName(originid);
		((ProgrammerFactory)userFactory).getOrCreateProgrammerForName(userid); 
		ToolUsePreference use = new ToolUsePreference(originid,userid,useCount);
		
		Set<Long> users = itemsToUsers.get(originid);
		if(users == null){
			users = new HashSet<Long>();
			itemsToUsers.put(originid,users);
		}
		users.add(userid);
		
		return use;
	}

}
