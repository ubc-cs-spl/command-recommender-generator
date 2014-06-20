package ca.ubc.cs.commandrecommender.generator;


import ca.ubc.cs.commandrecommender.db.IRecommenderDB;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;

public class LintonTotalRecommendation extends LintonRecommendation{

    public LintonTotalRecommendation(IRecommenderDB db, String algorithm) {
        super(db, algorithm);
    }

    @Override
    public void trainWith(ToolUseCollection tools) {
        toolCount.addAll(tools.toolsUsedBag());
    }

}