package net.therap.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
@SQLDelete(sql = "UPDATE final_learnhub_course SET is_deleted = 1 WHERE id = ?")
@Where(clause = "is_deleted = 0")
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
}