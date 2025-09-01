package net.therap.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Table(name = "final_learnhub_course")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course extends Persistent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "final_learnhub_course_seq_gen")
    @SequenceGenerator(name = "final_learnhub_course_seq_gen", sequenceName = "final_learnhub_course_seq", allocationSize = 1)
    private long id;
    
    @Column(length = 128, nullable = false)
    private String name;
    
    @Lob
    @Column
    private String description;
    
    @Column(nullable = false, name = "current_release")
    private long currentRelease;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "instructor_id", referencedColumnName = "id")
    private Instructor instructor;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules;

    public List<Module> getModules() {
        return modules.stream()
                .filter(module -> !module.isDeleted())
                .collect(Collectors.toList());
    }
}