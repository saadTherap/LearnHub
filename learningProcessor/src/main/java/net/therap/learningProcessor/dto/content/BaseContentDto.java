package net.therap.learningProcessor.dto.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import net.therap.learningProcessor.eum.CompletionStatus;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseContentDto {

    private Long id;
    private String title;
    private Long moduleId;
    private long orderIndex;
    private String type;
    private CompletionStatus status;
}