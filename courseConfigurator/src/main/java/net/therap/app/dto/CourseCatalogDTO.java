package net.therap.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO for public course catalog listings.
 * Contains only essential information for browsing.
 *
 * @author gazizafor
 * @since 23/7/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseCatalogDTO {
    private long courseId;
    private String name;
    private String description;
    private String instructorName;
    private long currentPublishedVersion;
//    private List<String> modules;
}