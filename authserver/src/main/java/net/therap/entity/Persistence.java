package net.therap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author apurboturjo
 * @since 7/24/25
 */
@Getter
@Setter
@MappedSuperclass
public abstract class Persistence implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt;
    
    @Column(name = "is_deleted", nullable = false)
    protected Boolean isDeleted;
    
    @Version
    private Integer version;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    
}
