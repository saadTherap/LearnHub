package net.therap.learningProcessor.dto.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseContentDto implements Serializable {

    private Long id;

    private String title;

    private Long moduleId;

    private long orderIndex;

    private boolean completed = false;

    private String type;
}