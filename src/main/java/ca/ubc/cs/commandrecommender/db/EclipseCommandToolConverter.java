package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.ToolUse;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by Spencer on 6/20/2014.
 */
public class EclipseCommandToolConverter extends AbstractCommandToolConverter {
    public static final String USER_ID = "user_id";
    public static final String DESCRIPTION = "description";
    public static final String BINDING_USED = "bindingUsed";
    public static final String TIME = "time";
    public static final String COMMAND_ID = "command_id";

    public EclipseCommandToolConverter(IndexMap toolUseMap){
        super(toolUseMap);
    }

    public ToolUse convertToToolUse(Map<String, Object> toolUse) {
        Integer tool = toolUseMap.getItemByItemId((String) toolUse.get(DESCRIPTION));
        Timestamp time = new Timestamp((Long) toolUse.get(TIME));
        boolean hotKey = (Boolean)toolUse.get(BINDING_USED);
        return new ToolUse(time, tool, hotKey);
    }

    public ToolUse convertRecommendationToToolUse(Map<String, Object> toolUse){
        Integer tool = toolUseMap.getItemByItemId((String) toolUse.get(COMMAND_ID));
        return new ToolUse(null, tool, false);
    }


    public String getUserIdField() {
        return USER_ID;
    }

}
