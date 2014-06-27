package ca.ubc.cs.commandrecommender.model.learning;

import ca.pfv.spmf.Itemset;

/**
 * Provides additional features to {@link ca.pfv.spmf.Itemset}
 */
//TODO: check over
public class MyItemset extends Itemset {

	private static final long serialVersionUID = 6316951900924884075L;
	
	public final int userId;
	
	public MyItemset(int userId){
		this.userId = userId;
	}
	
	public MyItemset(Itemset i) {
		if(i instanceof MyItemset){
			userId = ((MyItemset)i).userId;
		}else{
			userId = -1;
		}
		setTimestamp(i.getTimestamp());
		getItems().addAll(i.getItems());		
	}

	public MyItemset union(Itemset other) {		
		MyItemset result = this.subtractedFrom(other);
		result.getItems().addAll(this.getItems());
		return result;
	}

	public boolean containsSameAs(Itemset other) {	
		return this.union(other).size()==this.size();
	}

	public MyItemset subtractedFrom(Itemset other) {
		MyItemset result;
		if(other instanceof MyItemset){
			result = new MyItemset(((MyItemset)other).userId);
		}else{
			result = new MyItemset(this.userId);
		}
			
		result.getItems().addAll(other.getItems());
		result.getItems().removeAll(this.getItems());
		return result;
	}
}