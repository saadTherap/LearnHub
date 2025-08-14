package net.therap.kafkaregistrytest.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import net.therap.learningProcessor.entity.EnrollmentNotification;
import net.therap.learningProcessor.eum.NotificationType;

import java.util.Objects;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@Setter
@Getter
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EnrollmentNotification.class, name = "ENROLLMENT"),
        @JsonSubTypes.Type(value = SubmissionNotification.class, name = "SUBMISSION")
})
public abstract class Notification {

    private Long id;

    private String message;

    private NotificationType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
