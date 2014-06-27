package ca.ubc.cs.commandrecommender.model.learning;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

//TODO: check over
public class Transaction{
	
	private final double[] toolsUsed;
	
	
	private int userId;
	private Timestamp lastTimeUsed;
	
	public Transaction(int numberOfTools){
		toolsUsed = new double[numberOfTools + 1];
	}
	
	public Transaction(int numberOfTools, int userId, Timestamp t, int toolId){
		this(numberOfTools);
		this.userId = userId;
		this.lastTimeUsed = t;
		add(toolId);
	}

    /**
     * The amount of time that need to pass for usages to be considered as being
     * in different transactions
     */
	private static final double tolerance =  3600000; //an hour

    /**
     * Determine whether a tool use should be included in this transaction
     * @param otherUserId
     * @param t
     * @return false if the userId is different, the timestamp is out of order, or
     * the time interval between the current and lastTimeUsed >= tolerance; true
     * otherwise
     */
	public boolean include(int otherUserId, Timestamp t){
		
		if(lastTimeUsed==null){
			lastTimeUsed = t;
			userId = otherUserId; 
			return true;
		}
		
		if(userId!=otherUserId){
			return false;
		}
		
		if(lastTimeUsed.after(t)){
			System.err.println("Unexpected time comparison");
			return false;//I don't think that this should happen
		}
		
		boolean shouldInclude = (t.getTime()-lastTimeUsed.getTime()) < tolerance;
		
		if(shouldInclude)
			lastTimeUsed = t;
		
		return shouldInclude;
	}
	
	public void add(int toolId) {
		toolsUsed[toolId] += 1;
		cacheValid = false;
	}
	
	private Set<Integer> toolsUsedCache = new HashSet<Integer>();
	private boolean cacheValid = true;
	
	public Set<Integer> toolsUsed(){
		
		if(!cacheValid){
			toolsUsedCache.clear();
			
			for(int j = 0; j < toolsUsed.length; j++){
				if(toolsUsed[j]>0){
					toolsUsedCache.add(j);
				}
			}
			
			cacheValid = true;
		}
			
		return new HashSet<Integer>(toolsUsedCache);
	}
	
	public boolean contains(int toolId){
		return toolsUsed[toolId] > 0.0;
	}
	
	public void removeAll(Iterable<Integer> toolCounts) {
		for(int toolToRemove : toolCounts){
			toolsUsed[toolToRemove] = 0.0;
		}
		cacheValid = false;
	}
	
	public int toolsUsedCount(){
		int result = 0;
		for(int j = 0; j < toolsUsed.length; j++){
			result += toolsUsed[j];
		}
		return result;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int j = 0; j < toolsUsed.length; j++){
			if(toolsUsed[j]>0){
				sb.append(j+"("+toolsUsed[j]+"),");
			}
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("]");
		return sb.toString();
	}
	
	public int getUserId(){
		return userId;
	}
}
