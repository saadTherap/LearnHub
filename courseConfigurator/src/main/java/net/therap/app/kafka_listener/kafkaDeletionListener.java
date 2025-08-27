package net.therap.app.kafka_listener;

import net.therap.app.model.Instructor;
import net.therap.app.service.InstructorService;
import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author tanvirhassan
 * @since 21/8/25
 */
@Component
public class kafkaDeletionListener {

    @Autowired
    private ProducerConsumerTask  producerConsumerTask;

    private final InstructorService  instructorService;

    public kafkaDeletionListener(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @KafkaListener(
            topics = "${kafka.topics.deletion}",
            groupId = "${kafka.topics.deletion.grp}"
    )
    void listen(String json) {
        String  email = producerConsumerTask.deserialize(json, String.class);

        Optional<Instructor> instructorOptional = instructorService.getByEmail(email);

        if (!instructorOptional.isPresent()) {
            throw new IllegalArgumentException("instructor with email " + email + " not found");
        }

        instructorService.deleteById(instructorOptional.get().getId());
    }
}
