package net.therap.auth.server.service;

import lombok.RequiredArgsConstructor;
import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author apurboturjo
 * @since 8/21/25
 */
@Service
@RequiredArgsConstructor
public class DeletionService {
    
    private final ProducerConsumerTask producerConsumerTask;
    
    @Value("${kafka.topics.student.deletion}")
    private String studentDeletionTopic;
    
    @Value("${kafka.topics.instructor.deletion}")
    private String instructorDeletionTopic;
    
    public void sendStudentDeletionInfo(String email) {
        producerConsumerTask.send(studentDeletionTopic, email);
    }
    
    public void sendInstructorDeletionInfo(String email) {
        producerConsumerTask.send(instructorDeletionTopic, email);
    }
}