package net.therap.learningProcessor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import net.therap.learningProcessor.dto.content.BaseContentDto;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleDto {

    private long id;
    private String title;
    private long courseId;

    private long numberOfContents;
    private List<BaseContentDto> contents;

    private int completedContentCount;
}