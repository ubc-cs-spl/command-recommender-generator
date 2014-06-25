package ca.ubc.cs.commandrecommender.model.learning;

import ca.pfv.spmf.Item;
import ca.pfv.spmf.Itemset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TODO: check over
public class SequenceSet implements Iterable<Sequence>{
	
	private ToolLearningDatabase sequences = new ToolLearningDatabase();
	private List<Itemset> itemsets = new ArrayList<Itemset>();
	
	public void process(Transaction t, int withinSessionId, boolean sameUser) {
		
		Itemset itemset = getItemset(t,withinSessionId);
		if(sameUser){
			itemsets.add(itemset);
		}else{
			itemsets.add(itemset);
			addSequences(t.getUserId());
		}
	}

	private void addSequences(int sequenceId) {
		Sequence s = new Sequence(sequenceId);
		for(Itemset i : itemsets){
			s.addItemset(i);
		}
		itemsets.clear();
	
		sequences.addIfSignificant(s);
	}

	@Override
	public Iterator<Sequence> iterator() {		
		return sequences.database().getSequences().iterator();
	}
	
	private static Itemset getItemset(Transaction t, int id) {
		
		Itemset i = new MyItemset(t.getUserId());
		i.setTimestamp(id);
		for(Integer toolId : t.toolsUsed()){
			i.addItem(new Item(toolId));
		}
		
		return i;
	}
}