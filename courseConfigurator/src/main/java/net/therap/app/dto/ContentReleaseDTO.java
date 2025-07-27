package net.therap.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // This field will indicate the actual type (LECTURE, QUIZ, SUBMISSION)
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LectureDTO.class, name = "LECTURE"),
        @JsonSubTypes.Type(value = QuizDTO.class, name = "QUIZ"),
        @JsonSubTypes.Type(value = SubmissionDTO.class, name = "SUBMISSION")
})
public abstract class ContentReleaseDTO {
    
    private long id;
    private long releaseNum;
    private long orderIndex;
    private String title;
    
    private long contentId;
    
    private String type;
}