package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.db.IRecommenderDB;
import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.TreeBag;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public abstract class LintonRecommendation extends AbstractRecGen {

	public LintonRecommendation(IRecommenderDB db, String algorithm) {
		super(db, algorithm);
	}

	protected Bag<Integer> toolCount = new TreeBag<Integer>();
	protected Set<Integer> mostPopularToolKeys = new TreeSet<Integer>();	
	
	@Override
	public void fillRecommendations(RecommendationCollector rc) {
		checkSorting();
		for(Integer toolToRecommend : mostPopularToolKeys){
			if(!rc.toolsContain(toolToRecommend)){
				rc.add(toolToRecommend, (double) toolCount.getCount(toolToRecommend));
				if(rc.isSatisfied())
					break;
			}			
		}
	}

    @Override
    public void runAlgorithm() {
        // Nothing to do here
    }

    /**
     * Ensures that {@link ca.ubc.cs.commandrecommender.generator.LintonRecommendation#mostPopularToolKeys}
     * are sorted by most popular ones first
     */
	private synchronized void checkSorting() {
		if(mostPopularToolKeys.size()!= toolCount.uniqueSet().size()){
			mostPopularToolKeys = new TreeSet<Integer>(new Comparator<Integer>() {
				public int compare(Integer a, Integer b) {
					//gotta use two because of infinite recursion
					int countA = toolCount.getCount(a);
					int countB = toolCount.getCount(b);
					
					//if the two are equal, don't return 0, 
					//or they'll be smooshed together!
					if(countA==countB)
						return a.compareTo(b);
					else
						return -(countA-countB);
				}
			});
			mostPopularToolKeys.addAll(toolCount.uniqueSet());
		}
	}

}