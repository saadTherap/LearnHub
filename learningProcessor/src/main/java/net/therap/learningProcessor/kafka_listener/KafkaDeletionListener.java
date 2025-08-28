package net.therap.learningProcessor.kafka_listener;

import net.therap.kafkaregistry.service.ProducerConsumerTask;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author tanvirhassan
 * @since 21/8/25
 */
@Component
public class KafkaDeletionListener {

    @Autowired
    private ProducerConsumerTask producerConsumerTask;

    private final StudentService  studentService;

    public KafkaDeletionListener(StudentService studentService) {
        this.studentService = studentService;
    }

    @KafkaListener(
            topics = "${kafka.topics.deletion}",
            groupId = "${kafka.topics.deletion.grp}"
    )
    void listen(String json) {
        String  email = producerConsumerTask.deserialize(json, String.class);

        StudentDto studentDto = studentService.getStudentByEmail(email);

        if (studentDto == null) {
            throw new IllegalArgumentException("student with email " + email + " not found");
        }

        studentService.deleteStudent(studentDto.getId());
    }
}
