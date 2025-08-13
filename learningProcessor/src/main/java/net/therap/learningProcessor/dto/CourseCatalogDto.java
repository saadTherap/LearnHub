package net.therap.learningProcessor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCatalogDto {

    private long courseId;

    private String name;

    private String description;

    private String instructorName;

    private int currentPublishedVersion;
}