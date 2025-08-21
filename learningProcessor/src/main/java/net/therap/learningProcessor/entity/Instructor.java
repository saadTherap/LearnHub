package net.therap.learningProcessor.entity;

/**
 * @author avidewan
 * @since 8/21/25
 */

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "final_learnhub_instructor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Instructor {

    @Id
    private Long id;

    private String name;

    private String email;
}