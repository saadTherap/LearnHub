package net.therap.app.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import net.therap.app.validation.OnUpdate;

import java.io.Serializable;

/**
 * @author gazizafor
 * @since 7/8/25
 */
@Data
public class ReorderDTO implements Serializable {
    
    @Min(value = 0, message = "{validation.id.min}", groups = {OnUpdate.class})
    private long id;
    
    @Min(value = 0, message = "{validation.order.index.min}", groups = {OnUpdate.class})
    private long orderIndex;
}