package net.therap.learningProcessor.eum;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum NotificationType {

    ENROLLMENT,

    SUBMISSION
}
