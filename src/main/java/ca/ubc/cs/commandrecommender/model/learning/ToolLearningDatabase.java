package ca.ubc.cs.commandrecommender.model.learning;

import ca.pfv.spmf.Itemset;
import ca.pfv.spmf.SequenceDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TODO: check over
class ToolLearningDatabase implements Serializable{
	
	private static final long serialVersionUID = -2110708519038439942L;
	
	private static int sequenceCounter = 0;
	public static final int minSignificanceSize = 40;
	
	private SequenceDatabase sd = new SequenceDatabase();
	
	private boolean isSignificant(Sequence s) {
		return s.getItemsets().size() >= minSignificanceSize;
	}

	private List<Itemset> collapseItemsetsToWindow(Sequence s) {
		
		assert isSignificant(s);
		
		List<Itemset> newItemsets = new ArrayList<Itemset>(s.getItemsets().size()-minSignificanceSize);
		
		MyItemset nextItemset = firstNCondensed(s);
		
		for(int i = minSignificanceSize; i < s.getItemsets().size(); i++){
			Itemset it = s.getItemsets().get(i);
			nextItemset = nextItemset.union(it);	
			nextItemset.setTimestamp(i);
			newItemsets.add(nextItemset);
		}

		newItemsets.add(nextItemset);
		
		eliminateSequenceDups(newItemsets);
		
		return newItemsets;
	}

	private MyItemset firstNCondensed(Sequence s) {
		MyItemset nextItemset = new MyItemset(-1);
		for(int i = 0 ; i<minSignificanceSize; i++){
			Itemset it = s.getItemsets().get(i);
			nextItemset = nextItemset.union(it);
		}
		return nextItemset;
	}

	private void eliminateSequenceDups(List<Itemset> newItemsets) {
		Itemset last = null;
		for (Iterator<Itemset> iterator = newItemsets.iterator(); iterator.hasNext();) {
			Itemset next = iterator.next();
			if(last!=null){
				 if(next.size()==last.size()){
					 iterator.remove();
				 }
			}
			last = next;
		}
	}
	
	private List<Sequence> expand(Sequence s){

		List<Itemset> s2 = collapseItemsetsToWindow(s);
		
		List<Sequence> ss = new ArrayList<Sequence>();
		
		MyItemset last = null;
		for(Itemset i : s2){
			if(last!=null){
				Itemset diff = last.subtractedFrom(i);
				diff.setTimestamp(i.getTimestamp());
				Sequence seq = new Sequence(sequenceCounter++);
				last.setTimestamp(0);
				seq.addItemset(last);
				diff.setTimestamp(1);
				seq.addItemset(diff);
				ss.add(seq);
			}
			last = new MyItemset(i);
		}
		
		return ss;
	}

	public void addIfSignificant(Sequence s) {
		if(isSignificant(s)){
			for(Sequence sPrime : expand(s)){
				sd.addSequence(sPrime);
			}
		}
	}

	public SequenceDatabase database() {
		return sd;
	}

	public double minSupport() {
		return (double)2/(double)sd.size()-0.000000000000001;
	}
}