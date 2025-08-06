package net.therap.learningProcessor.entity;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@DiscriminatorValue("ENROLLMENT")
@Getter
@Setter
public class EnrollmentNotification extends Notification {

    private Long studentId;

    private Long courseId;
}
