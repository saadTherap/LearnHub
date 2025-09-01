package net.therap.learningProcessor.util;

import net.therap.learningProcessor.dto.content.submission.StudentSubmissionDto;
import net.therap.learningProcessor.entity.EnrollmentNotification;
import net.therap.learningProcessor.entity.Notification;
import net.therap.learningProcessor.entity.SubmissionNotification;
import net.therap.learningProcessor.eum.NotificationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * @author avidewan
 * @since 8/27/25
 */
class NotificationUtilTest {

    @Test
    void testCreateEnrollmentNotification() {
        Long courseId = 101L;
        Long studentId = 505L;
        String expectedMessage = "An student has enrolled into Course Id: " + courseId;

        Notification notification = NotificationUtil.createEnrollmentNotification(courseId, studentId);

        Assertions.assertNotNull(notification);
        Assertions.assertInstanceOf(EnrollmentNotification.class, notification);

        EnrollmentNotification enrollmentNotification = (EnrollmentNotification) notification;

        Assertions.assertEquals(NotificationType.ENROLLMENT, enrollmentNotification.getType());
        Assertions.assertEquals(studentId, enrollmentNotification.getStudentId());
        Assertions.assertEquals(courseId, enrollmentNotification.getCourseId());
        Assertions.assertEquals(expectedMessage, enrollmentNotification.getMessage());
    }

    @Test
    void testCreateSubmissionNotification() {
        Long submissionId = 202L;
        Long contentId = 303L;
        String expectedMessage = "An student has made a submission into content Id: " + contentId;

        StudentSubmissionDto submissionDto = StudentSubmissionDto.builder()
                .id(submissionId)
                .contentId(contentId)
                .submittedAt(LocalDateTime.now())
                .build();

        Notification notification = NotificationUtil.createSubmissionNotification(submissionDto);

        Assertions.assertNotNull(notification);
        Assertions.assertInstanceOf(SubmissionNotification.class, notification);

        SubmissionNotification submissionNotification = (SubmissionNotification) notification;

        Assertions.assertEquals(NotificationType.SUBMISSION, submissionNotification.getType());
        Assertions.assertEquals(submissionId, submissionNotification.getSubmissionId());
        Assertions.assertEquals(expectedMessage, submissionNotification.getMessage());
    }
}