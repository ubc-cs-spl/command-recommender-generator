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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToolUse toolUse = (ToolUse) o;

        if (tool != toolUse.tool) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = time.hashCode();
        result = 31 * result + tool;
        result = 31 * result + (hotkey ? 1 : 0);
        return result;
    }
}
