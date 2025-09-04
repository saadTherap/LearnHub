package net.therap.app.kafka_listener;

import lombok.extern.slf4j.Slf4j;
import net.therap.app.model.Instructor;
import net.therap.app.service.InstructorService;
import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author tanvirhassan
 * @since 19/8/25
 */
@Slf4j
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
    void listen(String json) {
        String  email = producerConsumerTask.deserialize(json, String.class);
        System.out.println(email);
        
        Optional<Instructor> instructorOptional = instructorService.getByEmail(email);
        
        if (instructorOptional.isPresent()) {
            log.info("[Kafka Registration Listener] Instructor already exists for {}", email);
            instructorOptional.get().setDeleted(false);
            instructorService.updateInstructor(instructorOptional.get());
        }
        
        Instructor instructor = new Instructor();
        instructor.setEmail(email);
        log.info("[Kafka Registration Listener] New Instructor created for {}", email);
        instructorService.createInstructor(instructor);
    }
}
