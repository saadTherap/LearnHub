package net.therap.learningProcessor.dto.content.lecture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.therap.learningProcessor.dto.content.ContentDetailDto;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LectureDto extends ContentDetailDto {

    private String description;

    private String videoUrl;

    private String resourceLink;
}