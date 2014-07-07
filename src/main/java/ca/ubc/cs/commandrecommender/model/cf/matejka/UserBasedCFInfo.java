package ca.ubc.cs.commandrecommender.model.cf.matejka;

/**
 * Encapsulate user-based CF related explanations
 * Created by KeEr on 2014-07-04.
 */
public class UserBasedCFInfo {

    //What percentage of users similar to you has used made this discovery or used this command
    public final double proportionOfSimilarUsersWhoUsedTheItem;
    //The top how many users similar to you are used for getting the result
    public final int numSimilarUsers;

    public UserBasedCFInfo(double proportionOfSimilarUsersWhoUsedTheItem, int numSimilarUsers) {
        this.proportionOfSimilarUsersWhoUsedTheItem = proportionOfSimilarUsersWhoUsedTheItem;
        this.numSimilarUsers = numSimilarUsers;
    }

}
