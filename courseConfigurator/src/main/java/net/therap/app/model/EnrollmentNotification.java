package net.therap.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("ENROLLMENT")
@Data
public class EnrollmentNotification extends Notification {

    @Column(nullable = false, name = "student_id")
    private Long studentId;
    
    @Column(nullable = false, name = "course_id")
    private Long courseId;
    
    @Override
    public String toString() {
        return "EnrollmentNotification{" + "studentId=" + studentId + ", courseId=" + courseId +'\n' + getMessage() + '}';
    }
}
