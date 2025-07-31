package net.therap.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO implements Serializable {
    private long id;
    private String title;
    private long moduleId;
    
    private ContentReleaseDTO currentContentRelease;
    private List<ContentReleaseDTO> contentReleases;
}