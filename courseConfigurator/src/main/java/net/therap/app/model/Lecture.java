package net.therap.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Entity
@Table(name = "final_learnhub_lecture")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("LECTURE")
public class Lecture extends ContentRelease {
    
    @Lob
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false, name = "video_url")
    private String videoUrl;
    
    @Column(nullable = false, name = "resource_link")
    private String resourceLink;
}