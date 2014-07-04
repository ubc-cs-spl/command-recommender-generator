package ca.ubc.cs.commandrecommender.model.cf.matejka;

import ca.ubc.cs.commandrecommender.model.cf.IPreferenceMapper;
import org.apache.mahout.cf.taste.model.Preference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * For recomputing the preferences of a IPreferenceMapper
 *
 * when recomputePreferences() is called, preferences are adjusted using Autodesk's 
 * method, described in:
 * CommunityCommands: Command Recommendations for Software Applications 
 * 
 * assumes each Preference contains the total number of uses of a tool
 * assumes that the Preferences in both of the constructor arguments are the same
 * 
 * @author emerson
 */
public class MatejkaPreferenceAdjustment{

	private final IPreferenceMapper<? extends Preference> mapper;

	public MatejkaPreferenceAdjustment(IPreferenceMapper<? extends Preference> mapper) {
		this.mapper = mapper;
	}
	
	/**
	 * Recomputes preferences value, based on Matejka's paper
	 */
	public void recomputePreferences() {
		
		Map<Preference, Float> newValues = new HashMap<Preference, Float>();
		
		//TODO: there's a more efficient way to do this
		for(Preference p : mapper.getPreferences()){
			
			float commandFrequencyInverseUserFrequency = 
				(float) (commandFrequency(p) * inverseUserFrequency(p));
				//if you're looking for the tuning param, it's in MatejkaSimilarity
			
			newValues.put(p, commandFrequencyInverseUserFrequency);
		}

        //we don't want to modify the preferences before all the calculations are done
		for(Map.Entry<Preference, Float> e : newValues.entrySet()){
			e.getKey().setValue(e.getValue());
		}
		
	}

	private double inverseUserFrequency(Preference p) {
		int usersInCommunity = mapper.getNumUsers(); 
		Preference[] prefs = mapper.getPreferenceForItem(p.getItemID());
		Set<Long> usersOfThisTool = new HashSet<Long>();
		for(Preference pref : prefs){
			usersOfThisTool.add(pref.getUserID());
		}
		return Math.log(usersInCommunity / (double)usersOfThisTool.size());
	}

	private double commandFrequency(Preference p) {
		double usesOfThisTool = p.getValue();
		double usesOfAllTools = 0;
		Preference[] preferences = mapper.getPreferenceForUser(p.getUserID());
		for(Preference pref : preferences){
			usesOfAllTools += pref.getValue();
		}
		return usesOfThisTool / usesOfAllTools;
	}
}
