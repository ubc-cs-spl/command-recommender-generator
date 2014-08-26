package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.Rationale;
import ca.ubc.cs.commandrecommender.model.RecommendationCollector;
import ca.ubc.cs.commandrecommender.model.ToolUse;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import ca.ubc.cs.commandrecommender.util.SortingUtils;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.SortedBag;
import org.apache.commons.collections4.bag.HashBag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Generate the commands with hot-keys that a user has never used.
 * a command by asking whether there is any user who used the
 * command with hotkey. This design may or may not be appropriate
 *
 * Deprecated because usage reports suggest hotkeys in a better manner
 */
@Deprecated
public class HotkeyRecGen extends AbstractRecGen {

    private Set<Integer> cmdsWithShortcuts;
    private HashMap<Integer, Bag<Integer>> userNeverHotkeyCmdMap;

    public HotkeyRecGen(String algorithm) {
        super(algorithm);
        cmdsWithShortcuts = new HashSet<Integer>();
        userNeverHotkeyCmdMap = new HashMap<Integer, Bag<Integer>>();
    }

    @Override
    public void trainWith(ToolUseCollection uses) {
        int user = uses.userId;
        Bag<Integer> usedCmds = new HashBag<Integer>();
        Set<Integer> usedWithHotkey = new HashSet<Integer>();
        for (ToolUse use : uses) {
            int tool = use.tool;
            usedCmds.add(tool);
            if (use.hotkey) {
                cmdsWithShortcuts.add(tool);
                usedWithHotkey.add(tool);
            }
        }
        for (Integer cmd : usedWithHotkey)
            usedCmds.remove(cmd); //removes all copies from bag
        userNeverHotkeyCmdMap.put(user, usedCmds);
    }

    @Override
    public void runAlgorithm() {
        //do nothing
    }

    @Override
    public void fillRecommendations(RecommendationCollector rc) {
        Bag<Integer> neverHotkey = userNeverHotkeyCmdMap.get(rc.userId);
        if (neverHotkey == null)
            return;
        SortedBag<Integer> neverHotkeyCmdsByFrequency = SortingUtils.sortBagByCount(neverHotkey);
        for (Integer tool : neverHotkeyCmdsByFrequency) {
            if (cmdsWithShortcuts.contains(tool)) {
                rc.add(tool, new Rationale((double) neverHotkeyCmdsByFrequency.getCount(tool)));
            }
        }
    }

}
