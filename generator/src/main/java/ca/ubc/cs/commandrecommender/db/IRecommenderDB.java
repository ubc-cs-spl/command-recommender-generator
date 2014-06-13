package ca.ubc.cs.commandrecommender.db;

import java.util.List;
import java.util.Set;

/**
 * Created by KeEr on 2014-06-12.
 */
public interface IRecommenderDB {

    List<String> getCmdsSortedByFrequency();

    List<String> getCmdsForWhichUserKnowsShortcut(String user);

    Set<String> getCmdsWithShortcuts();

    List<String> getAllUsers();

    Set<String> getUsedCmdsForUser(String user);

    Set<String> getAlreadyRecommendedCmdsForUser(String user);

    void insertRecommendation(String commandId, String reason, String user);

    void markAllRecommendationOld(String user);

    boolean shouldRecommendToUser(String user);

    void updateRecommendationStatus(String user);

}
