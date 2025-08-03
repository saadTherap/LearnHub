package net.therap.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import net.therap.app.validation.htmlSanitization.SanitizeHtml;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionCatalogueDTO extends ContentCatalogueDTO {
    
    @SanitizeHtml
    @NotBlank(message = "{validation.description.notblank}",  groups = OnCreate.class)
    @Size(min = 1, max = 250, message = "{validation.description.size}",   groups = {OnCreate.class, OnUpdate.class})
    private String description;
    
    @NotBlank(message = "{validation.url.notblank}", groups = OnCreate.class)
    @Size(min = 1, max = 250, message = "{validation.url.size}")
    private String resourceLink;
}