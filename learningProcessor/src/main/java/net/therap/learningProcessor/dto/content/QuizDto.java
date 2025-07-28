package net.therap.learningProcessor.dto.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizDto extends ContentDetailDto {

    private List<QuizQuestionDto> questions;
}
