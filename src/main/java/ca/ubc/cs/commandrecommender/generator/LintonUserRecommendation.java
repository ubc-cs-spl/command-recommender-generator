package ca.ubc.cs.commandrecommender.generator;


import ca.ubc.cs.commandrecommender.db.IRecommenderDB;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;

public class LintonUserRecommendation extends AbstractLintonRecGen {

    public LintonUserRecommendation(String algorithm) {
        super(algorithm);
    }

    @Override
    public void trainWith(ToolUseCollection tools) {
        toolCount.addAll(tools.toolsUsedBag().uniqueSet());
    }

}