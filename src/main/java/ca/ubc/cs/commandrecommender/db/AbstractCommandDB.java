package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.ToolUseCollection;

import java.util.List;

/**
 * Created by Spencer on 6/20/2014.
 */
public abstract class AbstractCommandDB {
    protected AbstractCommandToolConverter toolConverter;
    protected IndexMap userIndexMap;

    public AbstractCommandDB(AbstractCommandToolConverter toolConverter, IndexMap userIndexMap){
        this.toolConverter = toolConverter;
        this.userIndexMap = userIndexMap;
    }

    public abstract List<ToolUseCollection> getAllUsageData();
    public abstract ToolUseCollection getUsersUsageData(String userId);
}
