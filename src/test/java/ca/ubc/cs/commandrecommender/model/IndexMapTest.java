package ca.ubc.cs.commandrecommender.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Spencer on 6/20/2014.
 */
public class IndexMapTest {
    private String GENERIC_ID1 = "GENERIC_ID1";
    private String GENERIC_ID2 = "GENERIC_ID2";
    private String GENERIC_ID3 = "GENERIC_ID3";
    private IndexMap map;

    @Before
    public void setUp(){
        map = new IndexMap();
    }
    
    @Test
    public void testAddSingleItem(){
        Integer ItemIndex = map.addItem(GENERIC_ID1);
        assertItemAdded(ItemIndex, GENERIC_ID1);
    }

    @Test
    public void testAddItemMultiple(){
        Integer ItemIndexItemOne = map.addItem(GENERIC_ID1);
        Integer ItemIndexItemTwo = map.addItem(GENERIC_ID2);
        Integer ItemIndexItemThree = map.addItem(GENERIC_ID3);
        assertItemAdded(ItemIndexItemOne, GENERIC_ID1);
        assertItemAdded(ItemIndexItemTwo, GENERIC_ID2);
        assertItemAdded(ItemIndexItemThree, GENERIC_ID3);

    }

    @Test
    public void testAddSameItemTwice(){
        Integer ItemIndex = map.addItem(GENERIC_ID1);
        Integer ItemIndexSecondInsert = map.addItem(GENERIC_ID1);
        assertEquals(ItemIndex, ItemIndexSecondInsert);
    }

    @Test
    public void testGetItemAdded(){
        Integer ItemIndex = map.addItem(GENERIC_ID1);
        Integer indexReturned = map.getItemByItemId(GENERIC_ID1);
        assertEquals(ItemIndex, indexReturned);
    }

    @Test
    public void testGetItemNotAdded(){
        Integer ItemIndex = map.getItemByItemId(GENERIC_ID1);
        assertNotNull(ItemIndex);
        assertEquals(ItemIndex.intValue(), 0);
        Integer ItemIndexAfterInsert = map.getItemByItemId(GENERIC_ID1);
        assertEquals(ItemIndex, ItemIndexAfterInsert);
    }

    private void assertItemAdded(Integer ItemIndex, String expectedItemId) {
        assertNotNull(ItemIndex);
        assertEquals(map.getItemByIndex(ItemIndex), expectedItemId);
    }

}
