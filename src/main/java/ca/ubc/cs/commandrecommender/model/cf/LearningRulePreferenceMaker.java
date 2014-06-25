package ca.ubc.cs.commandrecommender.model.cf;

import java.util.Iterator;
import java.util.List;

/**
 * Created by KeEr on 2014-06-23.
 */

//TODO: check over
public class LearningRulePreferenceMaker extends PreferenceMaker {

    public boolean incrementUserPrefence(Long userid, Long itemid) {
        List<ToolUsePreference> list1 = usersToPrefs.get(userid);
        for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
            ToolUsePreference toolUse = (ToolUsePreference) iterator.next();
            if(toolUse.getItemID()==itemid){
                toolUse.setValue(toolUse.getValue()+1);
                return true;
            }
        }
        List<ToolUsePreference> list2 = toolsToPrefs.get(itemid);
        for (Iterator iterator = list2.iterator(); iterator.hasNext();) {
            ToolUsePreference toolUse = (ToolUsePreference) iterator.next();
            if(toolUse.getUserID()==userid){
                toolUse.setValue(toolUse.getValue()+1);
                return true;
            }
        }
        return false;
    }

}
