package net.therap.app.dto;

import lombok.*;

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
    private String title;
    private long courseId;
    private List<ContentDTO> contents;
}