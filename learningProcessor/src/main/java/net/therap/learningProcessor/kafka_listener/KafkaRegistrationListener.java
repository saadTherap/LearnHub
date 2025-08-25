package net.therap.learningProcessor.kafka_listener;

import net.therap.kafkaregistry.service.ProducerConsumerTask;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.service.StudentService;
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

    private final StudentService studentService;

    public KafkaRegistrationListener(StudentService studentService) {
        this.studentService = studentService;
    }

    @KafkaListener(
            topics = "${kafka.topics.registration}",
            groupId = "${kafka.topics.registration.grp}"
    )
    void listen(String json) {
        String  email = producerConsumerTask.deserialize(json, String.class);

        StudentDto studentDto = new StudentDto();
        studentDto.setEmail(email);

        studentService.createStudent(studentDto);
    }
}
