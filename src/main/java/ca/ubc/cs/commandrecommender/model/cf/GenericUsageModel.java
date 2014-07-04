package ca.ubc.cs.commandrecommender.model.cf;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
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

/**
 * A Generic Usage Model that allow easy customization to work with {@link org.apache.mahout.cf.taste.recommender.Recommender}
 * and {@link ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaPreferenceAdjustment}
 * @param <P>
 * @param <IF>
 * @param <UF>
 */
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

    // Please consult the documentation for the implemented interface for the following methods

    @Override
    public LongPrimitiveIterator getItemIDs() {
        //TODO: creating a new array just to get the length is somewhat wasteful
        //      should consider expending the ItemFactory class and modify its subclasses
        return new LongPrimitiveArrayIterator(itemFactory.tools());
    }

    @Override
    public int getNumItems() {
        return itemFactory.tools().length;
    }

    @Override
    public LongPrimitiveIterator getUserIDs() {
        //TODO: creating a new array just to get the length is somewhat wasteful
        //      should consider expending the UserFactory class and modify its subclasses
        return new LongPrimitiveArrayIterator(userFactory.users());
    }

    @Override
    public int getNumUsers() {
        return userFactory.users().length;
    }

    @Override
    public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
        //If the itemID is not in the model then we must have a bug somewhere in our code
        //The exception will not stop the application from running but will let us know
        //through logging whether the code is working as intended
        if (itemFactory.toolForToolID(itemID) == -1)
            throw new NoSuchItemException(itemID);
        P[] prefs = toolsToPrefs.get(itemID);
        if(prefs==null)
            return new GenericItemPreferenceArray(Collections.EMPTY_LIST);
        return new GenericItemPreferenceArray(Arrays.asList(prefs));
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
        return toolsToPrefs.get(itemID).length; //itemID should be valid
    }

    @Override
    public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
        Set<Long> userIntersection = new HashSet<Long>(itemsToUsers.get(itemID1));
        userIntersection.retainAll(itemsToUsers.get(itemID2));
        return userIntersection.size();
    }

    @Override
    public Iterable<P> getPreferences(){
        return new MappedArrayIterator<Long, P>(toolsToPrefs);
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        //do nothing
    }

    @Override
    public PreferenceArray getPreferencesFromUser(long userID)
            throws TasteException {
        //Although the specification says that we should throw
        //exception when the method is called with an unknown user,
        //it's fairly common for us to try to retrieve preferences for
        //a user unknown to the model we build. Thus, it make sense for
        //us to return an empty PreferenceArray in which case no recommendation
        //will be generated
        P[] prefs = usersToPrefs.get(userID);
        if(prefs==null)
            return new GenericUserPreferenceArray(Collections.EMPTY_LIST);
        return new GenericUserPreferenceArray(Arrays.asList(prefs));
    }

    @Override
    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
        if (userFactory.userForUserId(userID) == -1)
            throw new NoSuchUserException(userID);
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
        if (userFactory.userForUserId(userID) == -1)
            throw new NoSuchUserException(userID);
        P[] prefs = usersToPrefs.get(userID);
        for (P p : prefs) {
            if(p.getItemID()==itemID)
                return p.getValue();
        }
        return null;
    }

    @Override
    public P[] getPreferenceForUser(Long userid) {
        return usersToPrefs.get(userid);
    }

    @Override
    public P[] getPreferenceForItem(Long itemid){
        return toolsToPrefs.get(itemid);
    }


    //TODO: None of the UnsupportedOperationException has popped during testing.
    //      It is worthwhile to know how mahout uses these method and deal with
    //      such exceptions

    @Override
    public  void removePreference(long userID, long itemID) throws TasteException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPreference(long userID, long itemID, float value) throws TasteException{
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getPreferenceTime(long userID, long itemID)
            throws TasteException {
        //TODO: maybe we can just return null according to the specs as
        //      we don't know about the time
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPreferenceValues() {
        //TODO: this should either return true or be abstract
        //      as whether the implementation actually stores and returns
        //      distinct preferences is determined by the specific implementation.
        //      So far, we have actual values for the subclasses
        throw new UnsupportedOperationException();
    }

    @Override
    public float getMaxPreference() {
        //TODO: we don't really have a max preference for any of the subclasses.
        //      This is simply undefined.
        throw new UnsupportedOperationException();
    }

    @Override
    public float getMinPreference() {
        //TODO: would it be safe to return 0 here? Pretty should the preference does not
        //      go below 0. This method is probably not so helpful given getMaxPreference()
        //      is not implemented
        throw new UnsupportedOperationException();
    }

}