package net.therap.app.dto;

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
    
    private long courseId;
    
    private List<ContentDTO> contents;
}