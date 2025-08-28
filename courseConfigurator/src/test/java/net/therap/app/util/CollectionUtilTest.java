package net.therap.app.util;

import net.therap.app.dto.ReorderDTO;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author gazizafor
 * @since 28/8/25
 */
class CollectionUtilTest {
    
    @Test
    void isEmptyCollection_nullCollection_returnsTrue() {
        assertTrue(CollectionUtil.isEmptyCollection(null));
    }
    
    @Test
    void isEmptyCollection_emptyCollection_returnsTrue() {
        assertTrue(CollectionUtil.isEmptyCollection(Collections.emptyList()));
    }
    
    @Test
    void isEmptyCollection_nonEmptyCollection_returnsFalse() {
        Collection<String> collection = new ArrayList<>();
        collection.add("item");
        assertFalse(CollectionUtil.isEmptyCollection(collection));
    }
    
    @Test
    void isValidOrderedList_emptyList_returnsFalse() {
        assertFalse(CollectionUtil.isValidOrderedList(Collections.emptyList()));
    }
    
    @Test
    void isValidOrderedList_nullList_returnsFalse() {
        assertFalse(CollectionUtil.isValidOrderedList(null));
    }
    
    @Test
    void isValidOrderedList_validList_returnsTrue() {
        ReorderDTO item1 = new ReorderDTO();
        item1.setId(1L);
        item1.setOrderIndex(1L);
        
        ReorderDTO item2 = new ReorderDTO();
        item2.setId(2L);
        item2.setOrderIndex(2L);
        
        List<ReorderDTO> itemList = Arrays.asList(item1, item2);
        assertTrue(CollectionUtil.isValidOrderedList(itemList));
    }
    
    @Test
    void isValidOrderedList_listWithDuplicateIds_returnsFalse() {
        ReorderDTO item1 = new ReorderDTO();
        item1.setId(1L);
        item1.setOrderIndex(1L);
        
        ReorderDTO item2 = new ReorderDTO();
        item2.setId(1L); // Duplicate ID
        item2.setOrderIndex(2L);
        
        List<ReorderDTO> itemList = Arrays.asList(item1, item2);
        assertFalse(CollectionUtil.isValidOrderedList(itemList));
    }
    
    @Test
    void isValidOrderedList_listWithDuplicateOrderIndexes_returnsFalse() {
        ReorderDTO item1 = new ReorderDTO();
        item1.setId(1L);
        item1.setOrderIndex(1L);
        
        ReorderDTO item2 = new ReorderDTO();
        item2.setId(2L);
        item2.setOrderIndex(1L); // Duplicate Order Index
        
        List<ReorderDTO> itemList = Arrays.asList(item1, item2);
        assertFalse(CollectionUtil.isValidOrderedList(itemList));
    }
    
    @Test
    void isValidOrderedList_listWithNullItem_returnsFalse() {
        ReorderDTO item1 = new ReorderDTO();
        item1.setId(1L);
        item1.setOrderIndex(1L);
        
        List<ReorderDTO> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(null); // Null item
        
        assertFalse(CollectionUtil.isValidOrderedList(itemList));
    }
}