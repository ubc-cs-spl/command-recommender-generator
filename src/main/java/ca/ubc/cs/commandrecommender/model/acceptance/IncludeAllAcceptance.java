package ca.ubc.cs.commandrecommender.model.acceptance;

import ca.ubc.cs.commandrecommender.model.learning.Transaction;
import org.apache.commons.collections4.Bag;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * All new commands are considered as "learned" or "discovered"
 *
 * Created by KeEr on 2014-06-20.
 */
public class IncludeAllAcceptance extends AbstractLearningAcceptance {

    @Override
    public Collection<Integer> filterTransactions(List<Transaction> ts, Bag<Integer> toolBag){
        return new LinkedList<Integer>();
    }

}
