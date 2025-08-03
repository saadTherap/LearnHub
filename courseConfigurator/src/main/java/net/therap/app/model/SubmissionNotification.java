package net.therap.app.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
@Entity
@DiscriminatorValue("SUBMISSION")
public class SubmissionNotification extends Notification {

    private Long submissionId;
}
