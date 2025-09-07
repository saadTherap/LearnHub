package net.therap.auth.server.entity.interfaces;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
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
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt;
    
    @Column(name = "is_deleted")
    protected boolean isDeleted;
    
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