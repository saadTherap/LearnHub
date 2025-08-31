package net.therap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO extends ContentReleaseDTO {
    private String description;
    private String resourceLink;
    
    // Constructor to set type and call super
    public SubmissionDTO(Long id, Long releaseNum, long orderedIndex, String title, Long contentId, // <<< CHANGED: contentId
                         String description, String resourceLink) {
        super(id, releaseNum, orderedIndex, title, contentId, "SUBMISSION"); // Set type here, pass contentId
        this.description = description;
        this.resourceLink = resourceLink;
    }
}