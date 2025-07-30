package net.therap.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;

import java.io.Serializable;
import java.util.List;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Data
public class CourseDTO implements Serializable {
    
    private long id;
    
    @NotBlank(message = "{validation.name.notblank}", groups = OnCreate.class)
    @Size(min = 2, max = 30, message = "{validation.name.size}", groups = {OnCreate.class, OnUpdate.class})
    private String name;
    
    @NotBlank(message = "{validation.description.notblank}", groups = OnCreate.class)
    @Size(min = 1, max = 250, message = "{validation.description.size}",  groups = {OnCreate.class, OnUpdate.class})
    private String description;
    
    private long currentRelease;
    
    private long instructorId;
    
    private String instructorName;
    
    private List<ModuleDTO> modules;
}