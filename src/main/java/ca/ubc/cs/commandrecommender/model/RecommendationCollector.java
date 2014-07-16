package ca.ubc.cs.commandrecommender.model;

import java.util.*;

/**
 * This class acts as a container for recommendations and keeps track of
 * the user id and history.
 */
public class RecommendationCollector implements Iterable<Integer>{
	
	//TODO: simplify the structure
	private List<List<Integer>> recommendations;
	private List<Integer> currentList;
	private Double lastValue;
    private Map<Integer, Rationale> rationaleMap;
    private int count;

	public final int userId;

	private List<Integer> history;

    /**
     * Create a recommendation collector for a given user that recommends at most
     * 10 recommendations
     * @param userId
     * @param history
     */
	public RecommendationCollector(int userId, List<Integer> history) {
		this.userId = userId;
		this.history = history;
		init();
	}

	private void init(){
		recommendations = new ArrayList<List<Integer>>();
		currentList = new ArrayList<Integer>();
        rationaleMap = new HashMap<Integer, Rationale>();
		lastValue = null;
		count = 0;
	}

    @Override
	public Iterator<Integer> iterator() {
		
		flush();
		
		return  new Iterator<Integer>() {
			
			int pointer = 0;
			
			@Override
			public boolean hasNext() {
				return pointer < size();
			}

			@Override
			//TODO: improve the performance of this 
			public Integer next() {
				int i = 0;
				for(List<Integer> ls : recommendations)
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

    public void add(Integer thisKey, double thisVal) {
        add(thisKey, new Rationale(thisVal));
    }

    /**
     * Add a potential recommendation. The already recommended items will not be added
     * @param thisKey
     * @param rationale
     */
	public void add(Integer thisKey, Rationale rationale) {

        double thisValue = rationale.getDecisionPointValue();

		//make sure we haven't already added it
		for(List<Integer> ls : recommendations)
            if (ls.contains(thisKey))
                return;
		if (currentList.contains(thisKey))
            return;

        //add the key and keep track of last value
		if (lastValue != null && !lastValue.equals(thisValue)) {
			flush();
		}
		lastValue = thisValue;
		currentList.add(thisKey);
        //It makes sense to embed the ranking logic here because the rank is not
        //only specific to the algorithm used but also to the user
        rationaleMap.put(thisKey, rationale.setRank(size() + 1));
	}

	private void flush() {
		if (!currentList.isEmpty()) {
			Collections.sort(currentList);
            recommendations.add(currentList);
            count += currentList.size();
            currentList = new ArrayList<Integer>();
		}
	}
    
	// return # of elements in recommendations (ie. the # of recommendations so far
	private int size() {
		return count;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

    /**
     *
     * @param tool tool or command id
     * @return true if the user has used the tool before
     */
	public boolean toolsContain(int tool) {
		return history.contains(tool);
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

    public Map<Integer, Rationale> getRationales(){
        return rationaleMap;
    }


}
