package ca.ubc.cs.commandrecommender.model;

import java.util.*;

/**
 * This class acts as a container for recommendations and keeps track of
 * the user id and history.
 */
public class RecommendationCollector implements Iterable<Integer>{
	
	private List<Integer> recommendations;
	private List<Integer> currentList;
	private Double lastValue;
    private Map<Integer, Rationale> rationaleMap;
    private int count;
    private int capacity = -1; //negative means no limit

	public final int userId;

	private Set<Integer> history;

    /**
     * Create a recommendation collector for a given user
     * @param userId
     * @param history
     */
	public RecommendationCollector(int userId, Set<Integer> history) {
		this.userId = userId;
		this.history = history;
		init();
	}

    /**
     * Create a recommendation collector for a given user
     * @param userId
     * @param history
     * @param capacity the number of recommendations this recommendation collector
     *                 can store; may store more if the last recommendations to store
     *                 have the same rank
     */
    public RecommendationCollector(int userId, Set<Integer> history, int capacity) {
        this(userId, history);
        this.capacity = capacity;
        init();
    }

	private void init(){
		recommendations = new ArrayList<Integer>();
		currentList = new ArrayList<Integer>();
        rationaleMap = new HashMap<Integer, Rationale>();
		lastValue = null;
		count = 0;
	}

    @Override
	public Iterator<Integer> iterator() {
    	flush();
		return recommendations.iterator();
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

        if (capacity >= 0 && size() >= capacity) {
            return;
        }

        double thisValue = rationale.getDecisionPointValue();

		if (currentList.contains(thisKey) || recommendations.contains(thisKey)) {
            return;
        }

		// It's considered a fatal error for now if this constraint is not satisfied
		if (lastValue != null && thisValue > lastValue) {
            throw new IllegalArgumentException();
        }
		
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
            recommendations.addAll(currentList);
            count += currentList.size();
            currentList = new ArrayList<Integer>();
		}
	}
    
	// return # of elements in recommendations (ie. the # of recommendations so far
	public int size() {
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
