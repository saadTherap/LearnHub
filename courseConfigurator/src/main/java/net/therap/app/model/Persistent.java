package net.therap.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Getter
@MappedSuperclass
@Setter
public abstract class Persistent implements Serializable {
    
    @Version
    private long version;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date created;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updated;
    
    @Column(name = "is_deleted", columnDefinition = "NUMBER(1) DEFAULT 0", nullable = false)
    private boolean isDeleted;
    
    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        created = now;
        updated = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
    
}