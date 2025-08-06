package net.therap.learningProcessor.entity;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;

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