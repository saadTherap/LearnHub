package net.therap.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;

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
@Data
public class ModuleDTO implements Serializable {
    
    private long id;
    
    @Size(min = 1, max = 100, message = "{validation.title.size}", groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(message = "{validation.title.notblank}", groups = {OnUpdate.class})
    private String title;
    
    // cannot be zero or less
    @Min(value = 1, message = "{validation.course.id.null}")
    private long courseId;
    
    @Min(value = 0, message = "{validation.order.index.min}", groups = {OnCreate.class, OnUpdate.class})
    private long orderIndex;
    
    private List<ContentDTO> contents;
}