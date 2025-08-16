package net.therap.secureFileServer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author avidewan
 * @since 7/24/25
 */

@Getter
@Setter
@MappedSuperclass
public abstract class Persistent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        created = updated = LocalDateTime.now();
    }

    @PreUpdate
    protected void OnUpdate() {
        updated = LocalDateTime.now();
    }
}