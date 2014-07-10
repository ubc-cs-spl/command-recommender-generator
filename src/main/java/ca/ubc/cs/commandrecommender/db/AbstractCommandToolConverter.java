package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.ToolUse;

import java.util.Map;

/**
 * Created by Spencer on 6/20/2014.
 */
public abstract class AbstractCommandToolConverter {
    protected IndexMap toolUseMap;

    public AbstractCommandToolConverter(IndexMap toolUseMap){
        this.toolUseMap = toolUseMap;
    }

    public Integer convertCommandIdToIndex(String commandId) {
        return toolUseMap.getItemByItemId(commandId);
    }

    abstract ToolUse convertToToolUse(Map<String, Object> toolUse);
    abstract String getUserIdField();
    abstract String getCommandIdField();
    abstract String getHotkeyField();
    abstract String getTimeField();
}
