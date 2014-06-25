package ca.ubc.cs.commandrecommender.generator;


import ca.ubc.cs.commandrecommender.model.ToolUseCollection;

/**
 * This algorithm recommends the most frequently used commands that a user is not using
 */
public class LintonTotalRecGen extends AbstractLintonRecGen {

    public LintonTotalRecGen(String algorithm) {
        super(algorithm);
    }

    @Override
    public void trainWith(ToolUseCollection tools) {
        toolCount.addAll(tools.toolsUsedBag());
    }

}