package ca.ubc.cs.commandrecommender.generator;

import ca.pfv.spmf.Item;
import ca.pfv.spmf.Itemset;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import org.apache.commons.collections4.Bag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A variant of LearningRuleRecGen that recommends commend based on the number
 * of prerequisite commands a user use for learning a unknown command
 *
 * Created by KeEr on 2014-07-07.
 */
public class MostPrereqLearningRuleRecGen extends AbstractLearningRuleRecGen {

    protected final ArrayList<Set<Integer>> coll;

    public MostPrereqLearningRuleRecGen(String algorithm, AbstractLearningAcceptance acceptance) {
        super(algorithm, acceptance);
        int size = 10000;
        coll = new ArrayList<Set<Integer>>(size);
        for(int i = 0; i < size ; i++){
            coll.add(new HashSet<Integer>());
        }
    }

    /**
     * Process the sequence in a way such that eventually we can easily retrieve
     * the set of commands a user can learn from a given command.
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
