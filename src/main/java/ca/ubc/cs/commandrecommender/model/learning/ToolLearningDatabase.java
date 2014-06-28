package ca.ubc.cs.commandrecommender.model.learning;

import ca.pfv.spmf.Itemset;
import ca.pfv.spmf.Sequence;
import ca.pfv.spmf.SequenceDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Break down the sequences into the antecedent and consequent and store them properly
 */
class ToolLearningDatabase implements Serializable{
	
	private static final long serialVersionUID = -2110708519038439942L;
	
	private static int sequenceCounter = 0;

    /**
     * The number of itemsets considered as the knowledge base in a sequence
     */
	public static final int minSignificanceSize = 40;
	
	private SequenceDatabase sd = new SequenceDatabase();

    /**
     * A sequence qualifies learning related algorithms must have at least {@code minSignificanceSize}
     * itemsets. For any thing less than this amount, we have too little information to determine any
     * thing useful
     * @param s
     * @return
     */
	private boolean isSignificant(Sequence s) {
		return s.getItemsets().size() >= minSignificanceSize;
	}

    /**
     * Break the sequence into Itemsets such that each one contains all the elements in the
     * itemset before it. (ie: s = [I1, I2, I3, ..., I40, I41, ... In] then resulting list
     * would be [(I1 U I2 U ... U I40),(I1 U I2 U ... U I 41) ... (I1 U I2 U ... U In)]
     * @param s
     * @return
     */
	private List<Itemset> collapseItemsetsToWindow(Sequence s) {
		
		assert isSignificant(s);
		
		List<Itemset> newItemsets = new ArrayList<Itemset>(s.getItemsets().size()-minSignificanceSize);
		
		MyItemset nextItemset = firstNCondensed(s);

        newItemsets.add(nextItemset);
		
		for(int i = minSignificanceSize; i < s.getItemsets().size(); i++){
			Itemset it = s.getItemsets().get(i);
			nextItemset = nextItemset.union(it);	
			nextItemset.setTimestamp(i);
			newItemsets.add(nextItemset);
		}
		
		eliminateSequenceDups(newItemsets);
		
		return newItemsets;
	}

    /**
     * condense the first {@code minSignificanceSize} itemset in s into a knowledge base
     * @param s
     * @return
     */
	private MyItemset firstNCondensed(Sequence s) {
		MyItemset nextItemset = new MyItemset(-1);
		for(int i = 0 ; i<minSignificanceSize; i++){
			Itemset it = s.getItemsets().get(i);
			nextItemset = nextItemset.union(it);
		}
		return nextItemset;
	}

    /**
     * eliminate the itemsets where no learning occurred
     * @param newItemsets
     */
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

    /**
     * Transfer {@code s} into a list of antecedent consequent sets
     * @param s
     * @return
     */
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

    /**
     * from {@code s}, add in legitimate learning sequence
     * (antecedent consequent sets) into the database
     * @param s
     */
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