package ca.ubc.cs.commandrecommender.model.acceptance;

import ca.ubc.cs.commandrecommender.model.learning.Transaction;
import org.apache.commons.collections4.Bag;

import java.util.Collection;
import java.util.List;

/**
 * Determine when can count as "learning" in a set of usage data
 *
 * Created by KeEr on 2014-06-20.
 */
public abstract class AbstractLearningAcceptance {

    /**
     * Modifies {@code ts} based on criteria: ts will only contain toolUse that meet a
     * certain criteria.
     * As a convenience, also returns a list of all tools excluded
     */
    public abstract Collection<Integer> filterTransactions(List<Transaction> ts, Bag<Integer> toolBag);

    /**
     * Remove {@code toolCounts} from every transaction in {@code ts}
     * @param ts
     * @param toolCounts
     */
    protected void removeAllFrom(List<Transaction> ts, Iterable<Integer> toolCounts) {
        for(Transaction t : ts){
            t.removeAll(toolCounts);
        }
    }

}
