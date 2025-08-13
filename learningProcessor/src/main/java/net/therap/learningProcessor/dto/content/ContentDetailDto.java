package net.therap.learningProcessor.dto.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.therap.learningProcessor.dto.content.lecture.LectureDto;
import net.therap.learningProcessor.dto.content.quiz.QuizDto;
import net.therap.learningProcessor.dto.content.submission.SubmissionDto;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LectureDto.class, name = "LECTURE"),
        @JsonSubTypes.Type(value = QuizDto.class, name = "QUIZ"),
        @JsonSubTypes.Type(value = SubmissionDto.class, name = "SUBMISSION")
})
public abstract class ContentDetailDto extends BaseContentDto {
}