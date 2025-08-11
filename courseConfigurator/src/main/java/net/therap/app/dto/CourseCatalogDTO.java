package net.therap.app.dto;

import lombok.*;

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
@Data
public class CourseCatalogDTO {
    private long id;
    private String name;
    private String description;
    private String instructorName;
    private long currentPublishedVersion;
    private List<ModuleCatalogDTO> modules;
}