package net.therap.app.util;

import lombok.extern.slf4j.Slf4j;
import net.therap.app.dto.ReorderDTO;
import net.therap.app.model.QuizOption;
import net.therap.app.model.QuizQuestion;

import java.util.*;
import java.util.stream.Collectors;

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
    
    public static boolean isSameQuestions(List<QuizQuestion> list1, List<QuizQuestion> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        
        
        List<QuizQuestion> sortedList1 = list1.stream()
                .sorted(Comparator.comparing(QuizQuestion::getQuestionText))
                .toList();
        
        List<QuizQuestion> sortedList2 = list2.stream()
                .sorted(Comparator.comparing(QuizQuestion::getQuestionText))
                .toList();
        
        for (int i = 0; i < sortedList1.size(); i++) {
            QuizQuestion q1 = sortedList1.get(i);
            QuizQuestion q2 = sortedList2.get(i);
            
            if (!q1.getQuestionText().equals(q2.getQuestionText())) {
                return false;
            }
            
            List<QuizOption> options1 = q1.getOptions();
            List<QuizOption> options2 = q2.getOptions();
            
            if (options1.size() != options2.size()) {
                return false;
            }
            
            List<QuizOption> sortedOptions1 = options1.stream()
                    .sorted(Comparator.comparing(QuizOption::getOptionText))
                    .toList();
            
            List<QuizOption> sortedOptions2 = options2.stream()
                    .sorted(Comparator.comparing(QuizOption::getOptionText))
                    .toList();
            
            for (int j = 0; j < sortedOptions1.size(); j++) {
                QuizOption o1 = sortedOptions1.get(j);
                QuizOption o2 = sortedOptions2.get(j);
                
                if (!o1.getOptionText().equals(o2.getOptionText())) {
                    return false;
                }
                
                if (o1.isCorrect() != o2.isCorrect()) {
                    return false;
                }
            }
        }
        
        return true;
    }
}