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
	
	private static final double tolerance =  3600000;
	
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

	public void writeLineForMallet(OutputStreamWriter out, int sessionId) throws IOException {
		out.write("user"+encode(userId));
		out.write(" ");
		out.write("session"+encode(sessionId));
		for(int j = 0; j < toolsUsed.length; j++)			
			for(int k = 0; k < toolsUsed[j]; k++)
				out.write(" "+encode(j));
		
		out.write(System.getProperty("line.separator"));
	}

	//TODO: a bidirectional map is a better solution for these two
	public static String encode(int i) {
		String original = i+"";
		String result = "";
		for(char c : original.toCharArray()){
			switch(c){
				case '0': result += 'a'; break;
				case '1': result += 'b'; break;
				case '2': result += 'c'; break;
				case '3': result += 'd'; break;
				case '4': result += 'e'; break;
				case '5': result += 'f'; break;
				case '6': result += 'g'; break;
				case '7': result += 'h'; break;
				case '8': result += 'i'; break;
				case '9': result += 'j'; break;
				default: System.out.println("Unexpected character: " + c);
			}
		}
		return result;
	}
	
	public static int decode(String s) {
		String result = "";
		for(char c : s.toCharArray()){
			switch(c){
				case 'a': result += '0'; break;
				case 'b': result += '1'; break;
				case 'c': result += '2'; break;
				case 'd': result += '3'; break;
				case 'e': result += '4'; break;
				case 'f': result += '5'; break;
				case 'g': result += '6'; break;
				case 'h': result += '7'; break;
				case 'i': result += '8'; break;
				case 'j': result += '9'; break;
				default: System.out.println("Unexpected character: " + c);
			}
		}
		return Integer.parseInt(result);
	}
}
