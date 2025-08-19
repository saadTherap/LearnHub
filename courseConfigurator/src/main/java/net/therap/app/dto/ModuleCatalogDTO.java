package net.therap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleCatalogDTO implements Serializable {
    
    private long id;
    private String title;
    private long courseId;
    private long orderIndex;
    private List<ContentCatalogueDTO> contents;
}