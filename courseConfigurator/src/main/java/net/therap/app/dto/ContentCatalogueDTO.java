package net.therap.app.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LectureCatalogDTO.class, name = "LECTURE"),
        @JsonSubTypes.Type(value = QuizCatalogDTO.class, name = "QUIZ"),
        @JsonSubTypes.Type(value = SubmissionCatalogueDTO.class, name = "SUBMISSION")
})
public class ContentCatalogueDTO {
    
    private long id;
    private String title;
    private long orderIndex;
    private long moduleId;
    private String type;
}