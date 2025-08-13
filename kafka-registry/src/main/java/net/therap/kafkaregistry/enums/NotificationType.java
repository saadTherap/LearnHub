package net.therap.kafkaregistry.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author tanvirhassan
 * @since 13/8/25
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum NotificationType {

    ENROLLMENT,
    SUBMISSION
}
