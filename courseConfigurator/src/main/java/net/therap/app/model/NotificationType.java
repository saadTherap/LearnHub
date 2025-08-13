package net.therap.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author gazizafor
 * @since 6/8/25
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum NotificationType {
    
    ENROLLMENT,
    SUBMISSION
}