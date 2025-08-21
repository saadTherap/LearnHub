package net.therap.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author gazizafor
 * @since 8/21/25
 */

@Getter
@Setter
@Entity
@Table(name = "avi_student")
public class Student extends Persistent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq_gen")
    @SequenceGenerator(
            name = "student_seq_gen",
            sequenceName = "avi_student_seq",
            allocationSize = 1
    )
    private long id;

    private String firstName;

    private String lastName;
    
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;
}