package net.therap.learningProcessor.entity;

import jakarta.persistence.DiscriminatorValue;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@DiscriminatorValue("SUBMISSION")
public class SubmissionNotification extends Notification {

    private Long submissionId;
}