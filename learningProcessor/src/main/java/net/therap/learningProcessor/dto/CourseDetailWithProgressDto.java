package net.therap.learningProcessor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDetailWithProgressDto {

    private long id;

    private String name;

    private String description;

    private int currentRelease;

    private long instructorId;

    private String instructorName;

    private double progress;

    private List<ModuleWithProgressDto> modules;
}