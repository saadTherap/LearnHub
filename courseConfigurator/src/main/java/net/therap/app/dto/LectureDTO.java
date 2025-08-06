package net.therap.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureDTO extends ContentReleaseDTO implements Serializable {

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