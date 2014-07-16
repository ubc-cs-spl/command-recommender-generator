package ca.ubc.cs.commandrecommender.db;

import ca.ubc.cs.commandrecommender.model.IndexMap;
import ca.ubc.cs.commandrecommender.model.ToolUse;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Spencer on 6/20/2014.
 */
public class EclipseCommandToolConverterTest {
    public static final String DESCRIPTION = "DESCRIPTION";
    private EclipseCommandToolConverter toolConverter;
    private Long TIME = System.currentTimeMillis();
    private String USER_ID = "USER_ID";
    private  boolean BINDING_USED = true;
    private IndexMap toolIndexMap;
    private IndexMap userIndexMap;

    @Before
    public void setUp(){
        toolIndexMap = new IndexMap();
        userIndexMap = new IndexMap();
        toolConverter = new EclipseCommandToolConverter(toolIndexMap);
    }

    @Test
    public void testConvertToToolUseValidRow(){
        Map<String, Object> validMap = getValidMap();
        ToolUse toolUse = toolConverter.convertToToolUse(validMap);
        assertEquals(toolUse.hotkey, BINDING_USED);
        Timestamp timestamp = new Timestamp(TIME);
        assertEquals(toolUse.time, timestamp);
        String description = toolIndexMap.getItemByIndex(toolUse.tool);
        assertEquals(description,DESCRIPTION);
    }

    private Map<String, Object> getValidMap(){
        Map<String, Object> validMap = new HashMap<String, Object>();
        validMap.put(EclipseCommandToolConverter.USER_ID, USER_ID);
        validMap.put(EclipseCommandToolConverter.DESCRIPTION, DESCRIPTION);
        validMap.put(EclipseCommandToolConverter.BINDING_USED, BINDING_USED);
        validMap.put(EclipseCommandToolConverter.TIME, TIME);
        return validMap;
    }
}
