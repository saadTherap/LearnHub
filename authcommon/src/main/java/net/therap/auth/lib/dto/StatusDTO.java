package net.therap.auth.lib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author gazizafor
 * @since 20/8/25
 */
@AllArgsConstructor
@Data
public class StatusDTO implements Serializable {
    private String status;
}