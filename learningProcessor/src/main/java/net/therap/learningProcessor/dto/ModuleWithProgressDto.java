package net.therap.learningProcessor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import net.therap.learningProcessor.dto.content.BaseContentDto;

import java.io.Serializable;
import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleWithProgressDto implements Serializable {

    private long id;

    private long orderIndex;

    private String title;

    private long courseId;

    private int completedContentCount;

    private long numberOfContents;

    private List<BaseContentDto> contents;
}