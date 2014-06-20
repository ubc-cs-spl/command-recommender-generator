package ca.ubc.cs.commandrecommender.model;

import java.sql.Timestamp;

public class ToolUse {

    //TODO: change if necessary
    public Timestamp time;
    public int tool;
    public boolean hotkey;

    public ToolUse(Timestamp time, int tool, boolean hotkey) {
        this.time = time;
        this.tool = tool;
        this.hotkey = hotkey;
    }

    public String toString(){
        return Integer.toString(tool);
    }

}
