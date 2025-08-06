package net.therap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleCatalogDTO {
    
    private long id;
    private String title;
    private long courseId;
    private List<ContentCatalogueDTO> contents;
}