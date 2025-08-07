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
@DiscriminatorValue("SUBMISSION")
@Data
public class SubmissionNotification extends Notification {

    @Column(nullable = false)
    private Long submissionId;
    
    @Override
    public String toString() {
        return "SubmissionNotification{" + "submissionId=" + submissionId +'\n' + getMessage() + '}';
    }
}
