package net.therap.learningProcessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author avidewan
 * @since 7/28/25
 */
@Data
@AllArgsConstructor
public class StudentContentCompletionDto implements Serializable {

    private long studentId;

    private long contentId;
}