package net.therap.kafkaregistry.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tanvirhassan
 * @since 13/8/25
 */
@Getter
@Setter
public class EnrollmentNotification extends Notification {

    private Long studentId;

    private Long courseId;
}