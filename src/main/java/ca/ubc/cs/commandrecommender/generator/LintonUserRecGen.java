package ca.ubc.cs.commandrecommender.generator;


import ca.ubc.cs.commandrecommender.model.ToolUseCollection;

/**
 * This algorithm recommends the most widely used commands (most number of users uses it)
 * which the target user is not using
 */
public class LintonUserRecGen extends AbstractLintonRecGen {

    public LintonUserRecGen(String algorithm) {
        super(algorithm);
    }

    @Override
    public void trainWith(ToolUseCollection tools) {
        toolCount.addAll(tools.toolsUsedBag().uniqueSet());
    }

}