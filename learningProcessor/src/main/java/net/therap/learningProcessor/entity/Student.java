package net.therap.learningProcessor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.therap.learningProcessor.eum.Gender;

import java.time.LocalDate;

/**
 * @author avidewan
 * @since 7/24/25
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

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(length = 100)
    private String address;

    @Column(length = 1024)
    private String imageUrl;
}