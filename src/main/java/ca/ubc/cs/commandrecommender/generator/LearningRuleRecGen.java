package ca.ubc.cs.commandrecommender.generator;

import ca.pfv.spmf.Item;
import ca.pfv.spmf.Itemset;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import java.util.ArrayList;

/**
 * Recommends the most popular discoveries that a user has the prerequisites for.
 * For example, if we have A->C, C->D, A->C, A->B as our discovery pattern and Person1 only
 * used A, then we would recommend C first and then B but we won't recommend D because
 * the user does not know C yet.
 *
 * Created by KeEr on 2014-06-20.
 */
public class LearningRuleRecGen extends AbstractLearningRuleRecGen {

    protected final ArrayList<Bag<Integer>> coll;

    public LearningRuleRecGen(String algorithm, AbstractLearningAcceptance acceptance) {
        super(algorithm, acceptance);
        int size = 10000;
        coll = new ArrayList<Bag<Integer>>(size);
        for(int i = 0; i < size ; i++){
            coll.add(new HashBag<Integer>());
        }
    }

    /**
     * Process the sequence in a way such that eventually we can easily retrieve
     * learned commands and their corresponding weight from a single command.
     * @param antecedent
     * @param consequent
     */
    @Override
    protected void processSequence(Itemset antecedent, Itemset consequent) {
        for(Item anAnti : antecedent.getItems()){
            Integer toolId = anAnti.getId();
            for(Item aCons : consequent.getItems())
                coll.get(toolId).add(aCons.getId());
        }
    }

    @Override
    protected void addRecsTo(Iterable<Integer> tools, final Bag<Integer> tempRecs) {
        for(Integer toolId : tools){
            tempRecs.addAll(coll.get(toolId));
        }
    }

}
