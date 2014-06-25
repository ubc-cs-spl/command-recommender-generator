package ca.ubc.cs.commandrecommender.model.cf;

import java.util.HashSet;
import java.util.Set;

//TODO: check over
class ToolFactory implements ItemFactory {

	private Set<Long> toolset = new HashSet<Long>();

	public long getOrCreateToolForName(long id) {
		if (!toolset.contains(id)) {
			toolset.add(id);
		}
		return id;
	}

	public Long toolForToolID(Long itemID) {
		if (toolset.contains(itemID)) {
			return itemID;
		}
		return (long) -1;
	}

	public long[] tools() {
		
		Object[] a = toolset.toArray();
		
		//TODO: better way?
		long[] tools= new long[a.length];
		for (int i = 0; i < a.length; i++) {
			tools[i]= (Long) a[i];
		} 
		
		return tools;
	}
}