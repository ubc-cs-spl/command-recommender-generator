package ca.ubc.cs.commandrecommender.generator;

import ca.pfv.spmf.Item;
import ca.pfv.spmf.Itemset;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

/**
 * This algorithm recommend the most commonly "learned" or "discovered" commands that a user is not using
 *
 * Created by KeEr on 2014-06-20.
 */
public class MostPopularLearningRuleRecGen extends AbstractLearningRuleRecGen {

    private Bag<Integer> recs = new HashBag<Integer>();

    public MostPopularLearningRuleRecGen(String algorithm, AbstractLearningAcceptance acceptance) {
        super(algorithm, acceptance);
    }

    @Override
    protected void processSequence(Itemset antecedent, Itemset consequent) {
        for(Item aCons : consequent.getItems())
            recs.add(aCons.getId());
    }

    @Override
    protected void addRecsTo(Iterable<Integer> tools, final Bag<Integer> tempRecs) {
        tempRecs.addAll(recs);
    }

}
