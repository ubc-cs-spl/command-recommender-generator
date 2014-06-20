package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import ca.ubc.cs.commandrecommender.model.User;

import java.util.List;
import java.util.Set;

/**
 * Created by KeEr on 2014-06-12.
 */
public interface IRecommenderDB {

    //TODO: modify as needed

    /**
     * Get all usage data
     * @return
     */
    List<ToolUseCollection> getAllData();

    /**
     * Ensure that the database has been indexed correctly
     * for better query performance
     */
    void insureIndex();

    /**
     * Get the command ID that corresponds to a command code
     * @param cmdCode
     * @return
     */
    String getCmdId(int cmdCode);

    /**
     * Get the set of commands (represented by an integer) whose short cut is
     * known by a user
     * @param user
     * @return
     */
    Set<Integer> getCmdsForWhichUserKnowsShortcut(User user);

    /**
     * Get the set of commands (represented by an integer) that has shortcuts
     * @return A set of commands with shortcuts
     */
    Set<Integer> getCmdsWithShortcuts();

    //TODO: move this method out as needed, it's being put here now for convenience
    /**
     * Get all users in the database
     * @return
     */
    List<User> getAllUsers();

}
