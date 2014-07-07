package ca.ubc.cs.commandrecommender.generator;

import ca.pfv.spmf.Item;
import ca.pfv.spmf.Sequence;
import ca.ubc.cs.commandrecommender.model.Rationale;
import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.RecommendedItemWithRationale;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.cf.LearningModel;
import ca.ubc.cs.commandrecommender.model.cf.Pair;
import ca.ubc.cs.commandrecommender.model.cf.ReasonedRecommender;
import ca.ubc.cs.commandrecommender.model.cf.matejka.MatejkaOptions;
import ca.ubc.cs.commandrecommender.model.learning.MyItemset;

import java.util.List;

/**
 * The base class for Collaborative Filtering with Discovery (Learning) algorithms.
 * These algorithms take into consideration the order in which commands or tools
 * are used or "discovered". We then perform collaborative filtering on these
 * learning sequences we recorded to generate recommendations.
 *
 * Created by KeEr on 2014-06-23.
 */
public abstract class AbstractCFWithDiscoveryRecGen extends AbstractFilteredLearningRecGen {


    protected MatejkaOptions ops;

    private ReasonedRecommender recommender;

    private LearningModel model;

    public AbstractCFWithDiscoveryRecGen(String label, AbstractLearningAcceptance acceptance, MatejkaOptions ops) {
        super(label, acceptance);
        this.ops = ops;
    }

    /**
     * Use the {@link ca.ubc.cs.commandrecommender.generator.AbstractCFWithDiscoveryRecGen#trainer}
     * to produce a {@link ca.ubc.cs.commandrecommender.model.cf.LearningModel} with which
     * {@link ca.ubc.cs.commandrecommender.generator.AbstractCFWithDiscoveryRecGen#recommender} is
     * initialized by {@link ca.ubc.cs.commandrecommender.generator.AbstractCFWithDiscoveryRecGen#getRecommender(ca.ubc.cs.commandrecommender.model.cf.LearningModel)}.
     */
    @Override
    public void runAlgorithm() {

        model = new LearningModel();
        for(Sequence s : trainer){
            if(s.size() != 2){
                System.err.println("Sequence not in form antecedent->consequent:" + s);
                continue;
            }

            MyItemset antecedent = (MyItemset) s.get(0);
            MyItemset consequent = (MyItemset) s.get(1);

            if(antecedent.userId!=consequent.userId){
                System.err.println("unexpected user");
                continue;
            }

            int userId = antecedent.userId;

            for(Item a : antecedent.getItems()){
                for(Item b : consequent.getItems()){
                    Pair p = new Pair(a.getId(), b.getId());
                    model.makeUseOf(userId, p);
                }
            }
        }

        model.done();

        recommender = getRecommender(model);
        trainer = null;//don't need this anymore, so free up to save memory
    }

    /**
     * Fill {@code rc} with top recommendations determined by
     * {@link ca.ubc.cs.commandrecommender.generator.AbstractCFWithDiscoveryRecGen#recommender}
     * that the user has prerequisites for.
     * @param rc is modified
     */
    @Override
    public void fillRecommendations(RecommendationCollector rc) {

        try {
            //TODO: the 1000 is artibrary... how to do this better?
            List<RecommendedItemWithRationale> items = recommender.recommendWithRationale(rc.userId, 1000);
            for(RecommendedItemWithRationale item : items){
                Rationale rationale = item.getRationale();
                long itemID = item.getItemID();
                Pair lr = model.getLearningRuleFactory().pairForToolID(itemID) ;

                int a = lr.getLeft();
                int b = lr.getRight();

				// Only put one of the items in there
                rationale.setValue((double) item.getValue());
                if(rc.toolsContain(a) && !rc.toolsContain(b)){
                    rationale.put(Rationale.DISCOVERY_PREREQ, a);
                    rc.add(b, rationale);
                }else if(!rc.toolsContain(a)) {
                    rationale.put(Rationale.HOOK_FOR, b);
                    rc.add(a, rationale);
                }

                if(rc.isSatisfied()){
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Subclass implementation determines what recommender will be used
     * @param model
     * @return
     */
    protected abstract ReasonedRecommender getRecommender(LearningModel model);

}
