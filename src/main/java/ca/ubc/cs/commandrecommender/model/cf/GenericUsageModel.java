package ca.ubc.cs.commandrecommender.model.cf;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveArrayIterator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import java.util.*;

//TODO: check over
public abstract class GenericUsageModel<P extends Preference, IF extends ItemFactory, UF extends UserFactory> 
				implements DataModel, IPreferenceMapper<P>{

	protected Map<Long, P[]> usersToPrefs;
	protected Map<Long, P[]> toolsToPrefs;	
	protected Map<Long, Set<Long>> itemsToUsers = new HashMap<Long,Set<Long>>();	

	protected final UF userFactory;
	protected final IF itemFactory;
	
	public GenericUsageModel(UF ufac, IF ifac){
		userFactory = ufac;
		itemFactory = ifac;
	}

//	@Override
//	public I getItem(Object itemID) {
//		return itemFactory.toolForToolID(itemID);
//	}

	@Override
	public LongPrimitiveIterator getItemIDs() {		
		return new LongPrimitiveArrayIterator(itemFactory.tools());
	}

	@Override
	public int getNumItems() {
		return itemFactory.tools().length;
	}

	@Override
	public LongPrimitiveIterator getUserIDs() {
		return new LongPrimitiveArrayIterator(userFactory.users());
	}
	
	@Override
	public int getNumUsers() {
		return userFactory.users().length;
	}
	

	@Override
	public PreferenceArray getPreferencesForItem(long itemID){
		return new GenericItemPreferenceArray(Arrays.asList(toolsToPrefs.get(itemID)));
//		return new ArrayIterator<P>(toolsToPrefs.get(itemFactory.toolForToolID(itemID)));
	}

//	@Override
//	public P[] getPreferencesForItemAsArray(long itemID){		
//		return toolsToPrefs.get(itemFactory.toolForToolID(itemID));
//	}

	@Override
	public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
		return toolsToPrefs.get(itemID).length;
	}
	
	@Override
	public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
		Set<P> userIntersection = null;
		//TODO: need to use itemsto users here
		P[] users1 = toolsToPrefs.get(itemID1);
		P[] users2 = toolsToPrefs.get(itemID2);
		userIntersection = new HashSet<P>(Arrays.asList(users1));
		userIntersection.retainAll(Arrays.asList(users2));
//		if(userIntersection==null)
//			return 0;
//		else
			return userIntersection.size();
	}
	
//	@Override
//	public int getNumUsersWithPreferenceFor(Object... itemIDs)
//			throws TasteException {
//		
//		//TODO: this method is not in the api any more
//		
//		//not completely sure that this works
//		Set<U> userIntersection = null;
//		for(Object itemID : itemIDs){
//			Set<U> users = usersOf(itemID);
//			if(userIntersection==null)
//				userIntersection = new HashSet<U>(users);//users is unmodifiable
//			else
//				userIntersection.retainAll(users);
//		}
//		
//		if(userIntersection==null)
//			return 0;
//		else
//			return userIntersection.size();
//	}

//	private Set<U> usersOf(Object toolId){
//		I t = itemFactory.toolForToolID(toolId);
//		if(t==null)
//			return new HashSet<U>();
//		
//		return Collections.unmodifiableSet(itemsToUsers.get(t));
//	}
	
	@Override
	public P[] getPreferenceForUser(Long userid) {
		return usersToPrefs.get(userid);
	}
	
	@Override
	public P[] getPreferenceForItem(Long itemid){
		return toolsToPrefs.get(itemid);
	}
	
	public Iterable<P> getPreferences(){
		return new MappedArrayIterator<Long, P>(toolsToPrefs);
	}


	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		//do nothing
	}

	@Override
	public  void removePreference(long userID, long itemID) throws TasteException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setPreference(long userID, long itemID, float value) throws TasteException{
		throw new UnsupportedOperationException();
	}
	

	@Override
	public PreferenceArray getPreferencesFromUser(long userID)
			throws TasteException {
		P[] prefs = usersToPrefs.get(userID);
		if(prefs==null)
			return new GenericUserPreferenceArray(Collections.EMPTY_LIST);
		return new GenericUserPreferenceArray(Arrays.asList(prefs));
	}

	@Override
	public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
		P[] prefs = usersToPrefs.get(userID);
		FastIDSet idSet = new FastIDSet();
		
		for (P p : prefs) {
			idSet.add(p.getItemID());
		}
		return idSet;
	}

	@Override
	public Float getPreferenceValue(long userID, long itemID)
			throws TasteException {
		// TODO Auto-generated method stub
		P[] prefs = usersToPrefs.get(userID);
		for (P p : prefs) {
			if(p.getItemID()==itemID)
				return p.getValue();
		}
		return null;
	}

	@Override
	public Long getPreferenceTime(long userID, long itemID)
			throws TasteException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPreferenceValues() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
//		return false;
	}

	@Override
	public float getMaxPreference() {
		// TODO Auto-generated method stub
//		return 0;
		throw new UnsupportedOperationException();
	}

	@Override
	public float getMinPreference() {
		// TODO Auto-generated method stub
//		return 0;
		throw new UnsupportedOperationException();
	}
}
