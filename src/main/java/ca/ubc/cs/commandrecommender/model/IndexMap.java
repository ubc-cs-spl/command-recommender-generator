package ca.ubc.cs.commandrecommender.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Created by Spencer on 6/23/2014.
 */
public class IndexMap {
    private BiMap<Integer, String> itemMap;
    private Integer currentIndex;

    public IndexMap(){
        itemMap = HashBiMap.create();
        currentIndex = 0;
    }

    public Integer addItem(String itemId){
        try {
            itemMap.put(currentIndex, itemId);
            currentIndex++;
            return currentIndex-1;
        }catch(IllegalArgumentException ex){
            return itemMap.inverse().get(itemId);
        }
    }

    public String getItemByIndex(Integer itemIndex){
        return itemMap.get(itemIndex);
    }

    /**
     * get the integer corresponding to {@code itemId}. if such integer doesn't exist,
     * add the {@code itemId} into the map
     * @param itemId
     * @return
     */
    public Integer getItemByItemId(String itemId){
        Integer itemIndex = itemMap.inverse().get(itemId);
        if(itemIndex == null){
            itemIndex = this.addItem(itemId);
        }
        return itemIndex;
    }
}
