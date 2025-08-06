package net.therap.app.model.enums;

import lombok.Getter;

/**
 * @author gazizafor
 * @since 31/7/25
 */
@Getter
public enum ContentType {
    
    LECTURE("Lecture"),
    
    SUBMISSION("Submission"),
    
    QUIZ("Quiz");
    
    private ContentType(String name) {
        this.name = name;
    }
    
    public static ContentType fromString(String contentType) {
        for (ContentType type : ContentType.values()) {
            if (type.name().equalsIgnoreCase(contentType)) {
                return type;
            }
        }
        
        return null;
    }
    
    private final String name;
}