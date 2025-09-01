package net.therap.app.util;

import lombok.extern.slf4j.Slf4j;
import net.therap.app.dto.ReorderDTO;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * @author gazizafor
 * @since 6/8/25
 */
@Slf4j
public class CollectionUtil {
    
    public static boolean isEmptyCollection(Collection collection) {
        return collection == null || collection.isEmpty();
    }
    
    public static boolean isValidOrderedList(List<ReorderDTO> itemList) {
        if (isEmptyCollection(itemList)) {
            return false;
        }
        
        Set<Long> uniqueIds = new HashSet<>();
        Set<Long> uniqueOrderIndexes = new HashSet<>();
        
        for (ReorderDTO item : itemList) {
            if (isNull(item)) {
                return false;
            }
        
            if (!uniqueIds.add(item.getId())) {
                return false;
            }
            
            if (!uniqueOrderIndexes.add(item.getOrderIndex())) {
                return false;
            }
        }
        
        return true;
    }
}