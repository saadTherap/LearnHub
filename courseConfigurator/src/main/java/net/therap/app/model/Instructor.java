package net.therap.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "final_learnhub_instructor_seq_gen")
    @SequenceGenerator(name = "final_learnhub_instructor_seq_gen", sequenceName = "final_learnhub_instructor_seq",
            allocationSize = 1)
    private long id;
    
    @Column(nullable = false)
    private String name;
    
    @ToString.Exclude
    @OneToMany(orphanRemoval = true, mappedBy = "instructor")
    private List<Course> courses;
    
//    @Override
//    public String toString() {
//        return name + " " + courses.size();
//    }
}