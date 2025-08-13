package net.therap.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gazizafor
 * @since 30/7/25
 */
@Data
public class InstructorDtoCatalog implements Serializable {
    
    private long id;
    
    private String name;
    
    private String email;
    
    private String imageUrl;
}