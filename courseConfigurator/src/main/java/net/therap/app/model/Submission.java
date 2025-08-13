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
@Entity(name = "final_learnhub_submission")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("SUBMISSION")
@PrimaryKeyJoinColumn(name = "id")
public class Submission extends ContentRelease {
    
    @Lob
    @Column(nullable = false, length = 255)
    private String description;
    
    @Column(nullable = false)
    private String resourceLink;
}