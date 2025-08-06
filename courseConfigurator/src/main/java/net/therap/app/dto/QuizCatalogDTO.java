package net.therap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gazizafor
 * @since 27/7/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizCatalogDTO extends ContentCatalogueDTO {
    
    private List<QuizQuestionDTO> questions;
}