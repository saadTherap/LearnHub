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
    
    @NotBlank(message = "{validation.description.notblank}")
    @Size(min = 1, max = 250, message = "{validation.description.size}")
    private String description;
    
    @NotBlank(message = "{validation.lecture.videoUrl.notblank}")
    @Size(min = 1, max = 250, message = "{validation.url.size}")
    private String videoUrl;
    
    @NotBlank(message = "{validation.lecture.resourceLink.notblank}")
    @Size(min = 1, max = 250, message = "{validation.url.size}")
    private String resourceLink;
    
    public LectureDTO(Long id, Long releaseNum, long orderedIndex, String title, Long contentId, // <<< CHANGED: contentId
                      String description, String videoUrl, String resourceLink) {
        super(id, releaseNum, orderedIndex, title, contentId, "LECTURE"); // Set type here, pass contentId
        this.description = description;
        this.videoUrl = videoUrl;
        this.resourceLink = resourceLink;
    }
}