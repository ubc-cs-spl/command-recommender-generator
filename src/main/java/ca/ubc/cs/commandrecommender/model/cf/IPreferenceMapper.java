package ca.ubc.cs.commandrecommender.model.cf;

import org.apache.mahout.cf.taste.model.Preference;

//TODO: check over
public interface IPreferenceMapper<P extends Preference>{
	public Iterable<P> getPreferences();
	public P[] getPreferenceForItem(Long itemid);
	public P[] getPreferenceForUser(Long userid);
	public int getNumUsers();
}