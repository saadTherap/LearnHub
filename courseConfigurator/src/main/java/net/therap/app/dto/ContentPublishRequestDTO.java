package net.therap.app.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.util.Map;

/**
 * @author gazizafor
 * @since 12/8/25
 */
@Data
public class ContentPublishRequestDTO implements Serializable {
    
    @NotNull(message = "{validation.content.ids.notnull}")
    @NotEmpty(message = "{validation.content.ids.notempty}")
    private Map<@Positive(message = "{validation.content.id.positive}") Long, String> contents;
}