package net.therap.learningProcessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import net.therap.learningProcessor.eum.CompletionStatus;

/**
 * @author avidewan
 * @since 7/28/25
 */
@Data
@AllArgsConstructor
public class StudentContentStatusDto {

    private long studentId;
    private long contentId;
    private CompletionStatus status;
}