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
public class CourseDTO implements Serializable {
    private long id;
    private String name;
    private String description;
    private long currentRelease;
    
    private long instructorId;
    private String instructorName;
    
    private List<ModuleDTO> modules;
}