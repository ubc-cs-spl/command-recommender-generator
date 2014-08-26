package ca.ubc.cs.commandrecommender.generator;

import ca.pfv.spmf.Item;
import ca.pfv.spmf.Itemset;
import ca.ubc.cs.commandrecommender.model.Rationale;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import java.util.List;

/**
 * This algorithm recommend the most commonly "learned" or "discovered" commands that a user is not using
 * The most popular commands beyond people's knowledge base
 */
public class MostPopularLearningRuleRecGen extends AbstractLearningRuleRecGen {

    private int totalLearning = 0;
    private Bag<Integer> discoveryTally = new HashBag<Integer>();

    public MostPopularLearningRuleRecGen(String algorithm, AbstractLearningAcceptance acceptance) {
        super(algorithm, acceptance);
    }

    @Override
    protected void processSequence(Itemset antecedent, Itemset consequent) {
        List<Item> items = consequent.getItems();
        totalLearning += items.size();
        for(Item aCons : items)
            discoveryTally.add(aCons.getId());
    }

    @Override
    protected void addRecsTo(Iterable<Integer> tools, final Bag<Integer> tempRecs) {
        tempRecs.addAll(discoveryTally);
    }

    @Override
    protected void updateRationale(Rationale rationale, int toolId) {
        double numberOfToolUses = discoveryTally.getCount(toolId);
        double percent = (numberOfToolUses / totalLearning) * 100;
        rationale.put(Rationale.MOST_POP_LEARNING_PERCENT, percent);
        rationale.setValueForTypeSpecificReason(percent);
    }

}
