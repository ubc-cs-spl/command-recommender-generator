package ca.ubc.cs.commandrecommender.model.cf;

import ca.ubc.cs.commandrecommender.model.RecommendedItemWithRationale;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.util.List;

/**
 * A recommender that provides a rationale with its recommendations
 */
public interface ReasonedRecommender extends Recommender {

    List<RecommendedItemWithRationale> recommendWithRationale(long userId, int howMany)
            throws TasteException;

}