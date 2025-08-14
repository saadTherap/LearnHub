package net.therap.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import net.therap.app.validation.htmlSanitization.SanitizeHtml;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author gazizafor
 * @since 13/8/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturePostDTO extends ContentCatalogueDTO {
    
    @SanitizeHtml
    @NotBlank(message = "{validation.description.notblank}",  groups = OnCreate.class)
    @Size(min = 1, max = 250, message = "{validation.description.size}",   groups = {OnCreate.class, OnUpdate.class})
    private String description;
    
    @NotNull(message = "{validation.video.required}", groups = OnCreate.class)
    private MultipartFile videoFile;
    
    @NotNull(message = "{validation.resource.required}", groups = OnCreate.class)
    private MultipartFile resourceFile;
}