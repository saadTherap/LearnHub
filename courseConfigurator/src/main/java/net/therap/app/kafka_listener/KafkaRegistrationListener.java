package net.therap.app.kafka_listener;

import net.therap.app.model.Instructor;
import net.therap.app.service.InstructorService;
import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author tanvirhassan
 * @since 19/8/25
 */
@Component
public class KafkaRegistrationListener {

    @Autowired
    private ProducerConsumerTask producerConsumerTask;

    private final InstructorService  instructorService;

    public KafkaRegistrationListener(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @KafkaListener(
            topics = "${kafka.topics.registration}",
            groupId = "${kafka.topics.registration.grp}"
    )
    void listen(String email) {
        Instructor instructor = new Instructor();
        instructor.setEmail(email);

        instructorService.createInstructor(instructor);
    }
}
