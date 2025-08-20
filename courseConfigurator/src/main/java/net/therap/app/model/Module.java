package net.therap.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Entity
@Table(name = "final_learnhub_module")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Module extends Persistent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "final_learnhub_module_seq_gen")
    @SequenceGenerator(name = "final_learnhub_module_seq_gen", allocationSize = 1, sequenceName =
            "final_learnhub_module_seq")
    private long id;
    
    @Column(nullable = false, length = 128)
    private String title;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "module")
    private List<Content> contents;
    
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course;
    
    @Column(name = "order_index")
    private long orderIndex;
    
    public List<Content> getContents() {
        return contents.stream()
                .filter(content -> !content.isDeleted())
                .toList();
    }
}