package net.therap.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureDTO extends ContentReleaseDTO {
    private String description;
    private String videoUrl;
    private String resourceLink;
    
    // Constructor to set type and call super
    public LectureDTO(Long id, Long releaseNum, long orderedIndex, String title, Long contentId, // <<< CHANGED: contentId
                      String description, String videoUrl, String resourceLink) {
        super(id, releaseNum, orderedIndex, title, contentId, "LECTURE"); // Set type here, pass contentId
        this.description = description;
        this.videoUrl = videoUrl;
        this.resourceLink = resourceLink;
    }
}