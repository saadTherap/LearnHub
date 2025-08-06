package net.therap.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
/**
 * @author gazizafor
 * @since 21/7/25
 */
@Entity(name = "final_learnhub_instructor")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Instructor extends Persistent {
    
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "final_learnhub_instructor_seq_gen"
    )
    @SequenceGenerator(
        name = "final_learnhub_instructor_seq_gen",
        sequenceName = "final_learnhub_instructor_seq",
        allocationSize = 1
    )
    private long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false, name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "image_url")
    private String imageUrl;

    @ToString.Exclude
    @OneToMany(orphanRemoval = true, mappedBy = "instructor")
    private List<Course> courses;
    
    @Override
    public String toString() {
        return "Instructor{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + ", dateOfBirth=" + dateOfBirth + ", imageUrl='" + imageUrl + '\'' + '}';
    }
}