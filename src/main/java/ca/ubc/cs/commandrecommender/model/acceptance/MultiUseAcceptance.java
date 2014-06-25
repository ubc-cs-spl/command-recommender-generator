package ca.ubc.cs.commandrecommender.model.acceptance;

import ca.ubc.cs.commandrecommender.model.learning.Transaction;
import org.apache.commons.collections4.Bag;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Only new commands used more than once are considered as "learned" or "discovered"
 *
 * Created by KeEr on 2014-06-20.
 */
public class MultiUseAcceptance extends AbstractLearningAcceptance {

    @Override
    public Collection<Integer> filterTransactions(List<Transaction> ts, Bag<Integer> toolBag){
        List<Integer> toRemove = new LinkedList<Integer>();

        for(Integer i : new HashSet<Integer>(toolBag.uniqueSet())){
            if(toolBag.getCount(i)<=1){
                toRemove.add(i);
            }
        }

        removeAllFrom(ts, toRemove);
        return toRemove;
    }
}
