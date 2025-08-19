package net.therap.auth.server.service;

import net.therap.kafkaregistry.service.ProducerConsumerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author apurboturjo
 * @since 8/19/25
 */
@Service
public class RegistrationService {
    
    @Autowired
    private ProducerConsumerTask producerConsumerTask;
    
    @Value("${kafka.topics.student.registration}")
    private String studentRegistrationTopic;

    @Value("${kafka.topics.instructor.registration}")
    private String instructorRegistrationTopic;
    
    public void sendStudentRegistrationInfo(String email) {
        producerConsumerTask.send(studentRegistrationTopic, email);
    }
    
    public void sendInstructorRegistrationInfo(String email) {
        producerConsumerTask.send(instructorRegistrationTopic, email);
    }
}