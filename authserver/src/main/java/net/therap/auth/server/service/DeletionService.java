package net.therap.auth.server.service;

import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author apurboturjo
 * @since 8/21/25
 */
@Service
public class DeletionService {
    
    @Autowired
    private ProducerConsumerTask producerConsumerTask;
    
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