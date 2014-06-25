package ca.ubc.cs.commandrecommender.model.cf;

import org.apache.mahout.cf.taste.model.Preference;

/**
 * Created by KeEr on 2014-06-23.
 */
//TODO: check over
public class ToolUsePreference implements Preference {

    private final long toolid;
    private final long userid;
    private float value;

    public ToolUsePreference(long toolid, long userid, int value) {
        this.toolid = toolid;
        this.userid = userid;
        this.value = value;
    }

    @Override
    public long getItemID() {
        return toolid;
    }

    @Override
    public long getUserID() {
        return userid;
    }

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public void setValue(float value) {
        this.value = value;
    }

    public String toString(){
        return userid+"@"+toolid + "="+value;
    }

}
