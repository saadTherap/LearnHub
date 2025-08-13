package net.therap.app.model.enums;

import lombok.Getter;

/**
 * @author gazizafor
 * @since 30/7/25
 */
@Getter
public enum ReleaseStatus {
    
    DRAFT(0),
    
    INITIAL_PUBLISHED(1);
    
    private final int releaseNumber;
    
    ReleaseStatus(int releaseNumber) {
        this.releaseNumber = releaseNumber;
    }
}