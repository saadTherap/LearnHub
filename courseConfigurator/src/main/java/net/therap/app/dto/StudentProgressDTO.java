package net.therap.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Data
public class StudentProgressDTO implements Serializable {
    
    private long studentId;
    private long courseId;
    private String firstName;
    private String lastName;
    private String email;
    private String courseName;
    private double progress;
}