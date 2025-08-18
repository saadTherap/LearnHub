package net.therap.app.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;

import java.io.Serializable;

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
public class ContentCatalogueDTO implements Serializable {
    
    private long id;
    
    @NotBlank(message = "{validation.title.notblank}", groups = OnCreate.class)
    @Size(min = 1, max = 100, message = "{validation.title.size}", groups = {OnCreate.class, OnUpdate.class})
    private String title;
    
    @Min(value = 0, message = "{validation.order.index.min}", groups = {OnCreate.class, OnUpdate.class})
    private long orderIndex;
    
    @Min(value = 1, message = "{validation.module.id.null}")
    private long moduleId;
    
    private String type;
}