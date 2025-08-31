package net.therap.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Entity
@Table(name = "final_learnhub_content_release")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "content_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ContentRelease extends Persistent {
    
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "final_learnhub_content_release_seq_gen"
    )
    @SequenceGenerator(
            name = "final_learnhub_content_release_seq_gen",
            sequenceName = "final_learnhub_content_release_seq",
            allocationSize = 1
    )
    private long id;
    
    @Column(name = "order_index")
    private long orderIndex;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", referencedColumnName = "id", nullable = false) // FK to the logical Content unit
    private Content content;
    
    @Column(name = "release", nullable = false)
    private long release;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (o instanceof ContentRelease contentRelease) {
            return this.id == contentRelease.id;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}