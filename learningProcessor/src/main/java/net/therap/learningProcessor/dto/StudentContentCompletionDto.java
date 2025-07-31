package net.therap.learningProcessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author avidewan
 * @since 7/28/25
 */
@Data
@AllArgsConstructor
public class StudentContentCompletionDto {

    private long studentId;
    private long contentId;
}