package ca.ubc.cs.commandrecommender.model.acceptance;

import ca.ubc.cs.commandrecommender.model.learning.Transaction;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Only new commands that are used in multiple sessions are considered as "learned" or "discovered"
 *
 * Created by KeEr on 2014-06-20.
 */
public class MultiSessionAcceptance extends AbstractLearningAcceptance {

    // Removes from all transactions all the tools that has only been used in one transaction
    @Override
    public Collection<Integer> filterTransactions(List<Transaction> ts, Bag<Integer> toolBag){

        Bag<Integer> toolCounts = new HashBag<Integer>();

        for(Transaction t : ts){
            toolCounts.addAll(t.toolsUsed());
        }
        //now the count of each item represents how many sessions it was in

        for(int toolId : new HashSet<Integer>(toolCounts.uniqueSet())){
            if(toolCounts.getCount(toolId) > 1){
                toolCounts.remove(toolId);
            }
        }
        //now the remaining items in the bag are just the single session ones

        removeAllFrom(ts, toolCounts);
        return toolCounts;
    }

}
