package net.therap.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * @author gazizafor
 * @since 24/7/25
 */
@Entity
@Table(name = "final_learnhub_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Content extends Persistent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "final_learnhub_content_seq_gen")
    @SequenceGenerator(
            name = "final_learnhub_content_seq_gen",
            sequenceName = "final_learnhub_content_seq",
            allocationSize = 1)
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id", nullable = false)
    private Module module;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_content_release_id", referencedColumnName = "id")
    private ContentRelease currentContentRelease;
    
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContentRelease> contentReleases;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (o instanceof Content content) {
            return this.id == content.getId();
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
//    public ContentRelease getCurrentContentRelease()
}