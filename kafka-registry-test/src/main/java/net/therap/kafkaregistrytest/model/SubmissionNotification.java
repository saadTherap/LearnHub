package net.therap.kafkaregistrytest.model;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;
import net.therap.learningProcessor.entity.Notification;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@DiscriminatorValue("SUBMISSION")
@Getter
@Setter
public class SubmissionNotification extends Notification {

    private Long submissionId;
}