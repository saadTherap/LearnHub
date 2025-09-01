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
public class LectureDTO extends ContentReleaseDTO {

    private String description;
    
    private String videoUrl;

    private String resourceLink;
    
    public LectureDTO(Long id, Long releaseNum, long orderedIndex, String title, Long contentId,
                      String description, String videoUrl, String resourceLink) {
        super(id, releaseNum, orderedIndex, title, contentId, "LECTURE");
        this.description = description;
        this.videoUrl = videoUrl;
        this.resourceLink = resourceLink;
    }
}