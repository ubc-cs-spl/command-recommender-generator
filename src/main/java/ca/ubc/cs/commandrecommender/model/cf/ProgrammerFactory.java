package ca.ubc.cs.commandrecommender.model.cf;

import java.util.HashSet;
import java.util.Set;

//TODO: check over
public class ProgrammerFactory implements UserFactory{
	
	private Set<Long> programmersSet = new HashSet<Long>();
	
	public Long getOrCreateProgrammerForName(Long userId ) {		
		
		if (!programmersSet.contains(userId)) {
			programmersSet.add(userId);
		}
		return userId;
	}
	
	public Long userForUserId(Long id) {
		if (programmersSet.contains(id)) {
			return id;
		}
		return (long) -1;
	}

	public long[] users() {
		Object[] a = programmersSet.toArray();
		
		//TODO: better way?
		long[] programmers= new long[a.length];
		for (int i = 0; i < a.length; i++) {
			programmers[i]= (Long)a[i];
		} 
		
		return programmers;
	}
}