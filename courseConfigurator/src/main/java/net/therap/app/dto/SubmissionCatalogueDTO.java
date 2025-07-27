package net.therap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionCatalogueDTO extends ContentCatalogueDTO {
    
    private String description;
    private String resourceLink;
}