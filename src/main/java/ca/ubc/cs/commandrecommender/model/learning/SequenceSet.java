package ca.ubc.cs.commandrecommender.model.learning;

import ca.pfv.spmf.Item;
import ca.pfv.spmf.Itemset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A model or database of the learning (tool use) sequences
 */
public class SequenceSet implements Iterable<Sequence>{
	
	private ToolLearningDatabase sequences = new ToolLearningDatabase();
	private List<Itemset> itemsets = new ArrayList<Itemset>();

    /**
     * Add the information from a transaction into the model
     * @param t
     * @param withinSessionId
     * @param last
     */
	public void process(Transaction t, int withinSessionId, boolean last) {
		Itemset itemset = getItemset(t,withinSessionId);
        itemsets.add(itemset);
		if(last){
			addSequences(t.getUserId());
		}
	}

    //put the current itemsets into sequences if significant and clear the itemsets for
    //future use
	private void addSequences(int sequenceId) {
		Sequence s = new Sequence(sequenceId);
		for(Itemset i : itemsets){
			s.addItemset(i);
		}
		itemsets.clear();
		sequences.addIfSignificant(s);
	}

    /**
     *
     * @return the Sequence(s) contained in the ToolLearningDatabase
     */
	@Override
	public Iterator<Sequence> iterator() {		
		return sequences.database().getSequences().iterator();
	}

    //get the itemset for a transaction
	private static Itemset getItemset(Transaction t, int id) {
		Itemset i = new MyItemset(t.getUserId());
		i.setTimestamp(id);
		for(Integer toolId : t.toolsUsed()){
			i.addItem(new Item(toolId));
		}
		return i;
	}

}