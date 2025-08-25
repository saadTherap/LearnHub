package net.therap.learningProcessor.util;

import net.therap.learningProcessor.dto.content.submission.StudentSubmissionDto;
import net.therap.learningProcessor.entity.EnrollmentNotification;
import net.therap.learningProcessor.entity.Notification;
import net.therap.learningProcessor.entity.SubmissionNotification;
import net.therap.learningProcessor.eum.NotificationType;

/**
 * @author avidewan
 * @since 8/25/25
 */
public class NotificationUtil {

    public static Notification createEnrollmentNotification(Long courseId, Long studentId) {
        EnrollmentNotification notification = new EnrollmentNotification();

        notification.setType(NotificationType.ENROLLMENT);
        notification.setStudentId(studentId);
        notification.setCourseId(courseId);
        notification.setMessage("An student has enrolled into Course Id: " + courseId);

        return notification;
    }

    public static Notification createSubmissionNotification(StudentSubmissionDto submissionDto) {
        SubmissionNotification notification = new SubmissionNotification();

        notification.setType(NotificationType.SUBMISSION);
        notification.setSubmissionId(submissionDto.getId());
        notification.setMessage("An student has made a submission into content Id: " + submissionDto.getContentId());

        return notification;
    }
}