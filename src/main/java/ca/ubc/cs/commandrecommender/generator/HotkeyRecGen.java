package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;

import java.util.Set;

/**
 * Generate the commands with hot-keys that a user has never used.
 *
 * Created by KeEr on 2014-06-09.
 */
public class HotkeyRecGen extends AbstractRecGen {

    //TODO: implement this class
    private Set<String> cmdsWithShortcuts;

    public HotkeyRecGen(String algorithm) {
        super(algorithm);
    }

    @Override
    public void fillRecommendations(RecommendationCollector rc) {

    }

    @Override
    public void trainWith(ToolUseCollection uses) {

    }

    @Override
    public void runAlgorithm() {

    }

}
