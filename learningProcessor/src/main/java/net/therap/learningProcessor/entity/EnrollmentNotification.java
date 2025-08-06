package net.therap.learningProcessor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@DiscriminatorValue("ENROLLMENT")
public class EnrollmentNotification extends Notification {

    private Long studentId;
}
