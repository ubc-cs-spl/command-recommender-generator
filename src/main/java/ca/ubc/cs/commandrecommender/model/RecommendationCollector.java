package ca.ubc.cs.commandrecommender.model;

import java.util.*;

/**
 * This class acts as a container for recommendations and keeps track of
 * the user id and history.
 */
public class RecommendationCollector implements Iterable<Integer>{

	private int recSize = 10;
	
	private List<List<Integer>> lists;
	private List<Integer> currentList;
	private Double lastValue;
    private Map<Integer, Rationale> rationaleMap;
    private boolean genAll;

	public final int userId;

	private List<Integer> history;
    private HashSet<Integer> pastRecommendations;

    /**
     * Create a recommendation collector for a given user that recommends at most
     * 10 recommendations
     * @param userId
     * @param history
     */
	public RecommendationCollector(int userId, List<Integer> history, HashSet<Integer> pastRecommendations) {
		this.userId = userId;
		this.history = history;
        this.pastRecommendations = pastRecommendations;
        this.genAll = false;
		init();
	}

    /**
     * Create a recommendation collector for a given user
     * @param userId user id
     * @param history usage data of the target user
     * @param size # of items to recommend
     */
	public RecommendationCollector(int userId, List<Integer> history,
                                   HashSet<Integer> recommendations, int size, boolean genAll) {
		this(userId,history,recommendations);
		recSize = size;
        this.genAll = genAll;
	}

	private void init(){
		lists = new ArrayList<List<Integer>>();
		currentList = new ArrayList<Integer>();
        rationaleMap = new HashMap<Integer, Rationale>();
		lastValue = null;
	}

    @Override
	public Iterator<Integer> iterator() {
		
		if(!isSatisfied())
			flush();
		
		return  new Iterator<Integer>() {
			
			int pointer = 0;
			
			@Override
			public boolean hasNext() {
				return pointer < size();
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

        //we don't add more than recSize
		if(isSatisfied())
            return;

        //filter out the already recommended ones
        if (pastRecommendations.contains(thisKey) && !genAll)
            return;

		//make sure we haven't already added it
		for(List<Integer> ls : lists)
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
			if(genAll || ((size() + currentList.size()) <= recSize))
                lists.add(currentList);
			else
                lists.add(currentList.subList(0, recSize-size()));
			currentList = new ArrayList<Integer>();
		}
	}

    /**
     * This method should be call only once when all candidate recommendations
     * have been added in to the collector
     */
    public void done() {
        flush();
    }


    /**
     *
     * @return whether or not we have made enough recommendations for the user
     */
	public boolean isSatisfied() {
		return !genAll && size() >= recSize;
	}

    // TODO: this method recomputes the current size of recommendation every time
    //       considering that we would usually have <= 10 recommendations, it's not
    //       that bad. Nevertheless, we might want to keep a counter for the size
    //       so we can retrieve the size more efficiently?
	// return # of elements in lists (ie. the # of recommendations so far
	private int size() {
		int size = 0;
		for(List<Integer> ls : lists)
			size += ls.size();
		return size;
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
