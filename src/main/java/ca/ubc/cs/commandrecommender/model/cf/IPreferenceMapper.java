package ca.ubc.cs.commandrecommender.model.cf;

import org.apache.mahout.cf.taste.model.Preference;

/**
 * Used for recomputing preferences using
 * {@link ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaPreferenceAdjustment}
 * @param <P>
 */
public interface IPreferenceMapper<P extends Preference>{

    /**
     *
     * @return all the known preferences
     */
	Iterable<P> getPreferences();

    /**
     *
     * @param itemId
     * @return all preferences related to itemId
     */
    P[] getPreferenceForItem(Long itemId);

    /**
     *
     * @param userId
     * @return all preferences related to userId
     */
    P[] getPreferenceForUser(Long userId);

    /**
     *
     * @return the total number of users
     */
    int getNumUsers();

}