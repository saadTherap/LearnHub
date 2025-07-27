package net.therap.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Entity(name = "final_learnhub_course")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course extends Persistent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "final_learnhub_course_seq_gen")
    @SequenceGenerator(name = "final_learnhub_course_seq_gen", sequenceName = "final_learnhub_course_seq", allocationSize = 1)
    private long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, length = 512)
    private String description;
    
    @Column(nullable = false, name = "current_release")
    private long currentRelease;
    
    @ManyToOne
    @JoinColumn(nullable = false, name = "instructor_id", referencedColumnName = "id")
    private Instructor instructor;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules;
}