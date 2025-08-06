package net.therap.app.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author tanvirhassan
 * @since 3/8/25
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "final_learnhub_notification_seq_gen")
    @SequenceGenerator(
            name = "final_learnhub_notification_seq_gen",
            sequenceName = "final_learnhub_notification_seq_gen",
            allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

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
