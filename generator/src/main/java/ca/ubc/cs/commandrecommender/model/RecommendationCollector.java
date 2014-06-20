package ca.ubc.cs.commandrecommender.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//TODO: renew
public class RecommendationCollector implements Iterable<Integer>{

	private int recSize = 10;
	
	private List<List<Integer>> lists;
	private List<Integer> currentList;
	private Double lastValue;

	public final int userId;
	private List<Integer> history;
	
	public RecommendationCollector(int user, List<Integer> history) {
		this.userId = user;
		this.history = history;
		init();
	}
	
	public RecommendationCollector(int userId, List<Integer> history, int size) {
		this(userId,history);
		recSize = size;
	}

	private void init(){
		lists = new ArrayList<List<Integer>>();
		currentList = new ArrayList<Integer>();
		lastValue = null;
	}

	public Iterator<Integer> iterator() {
		
		if(!isSatisfied())
			flush();
		
		return  new Iterator<Integer>() {
			
			int pointer = 0;
			
			@Override
			public boolean hasNext() {
				return pointer<size();
			}

			@Override
			public Integer next() {
				int i = 0;
				for(List<Integer> ls : lists)
					for(Integer l : ls){
						if(pointer==i){
							pointer++;
							return l;
						}else{
							i++;
						}
					}
				
				throw new IndexOutOfBoundsException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public void add(Integer thisKey, Double thisValue) {
		
		if(isSatisfied())
			return;
		
		//make sure we haven't already added it
		for(List<Integer> ls : lists)
			for(Integer i : ls)
				if(i.equals(thisKey))
					return;
		for(Integer i : currentList)
			if(i.equals(thisKey))
				return;
		
		if (lastValue != null && !lastValue.equals(thisValue)) {
			flush();
		}
		lastValue = thisValue;
		currentList.add(thisKey);
	}

	private void flush() {
		if (!currentList.isEmpty()) {
			Collections.sort(currentList);
			if(size()+currentList.size()>recSize)
				lists.add(currentList.subList(0, recSize-size()));
			else
				lists.add(currentList);
			currentList = new ArrayList<Integer>();
		}
	}

	public boolean isSatisfied() {
		return size() >= recSize;
	}

	// return # of elements in lists
	private int size() {
		int size = 0;
		
		for(List<Integer> ls : lists)
			size += ls.size();
		
		return size;
	}

	public boolean isEmpty() {
		return !iterator().hasNext();
	}

	public boolean toolsContain(int a) {
		return history.contains(a);
	}

	public Iterable<Integer> tools() {
		return history;
	}

	public boolean containsRec(Integer i) {
		for(Integer rec : this)
			if(rec.equals(i))
				return true;
		return false;
	}

}
