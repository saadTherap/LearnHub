package net.therap.app.helper;

import net.therap.app.model.Content;
import net.therap.app.model.ContentRelease;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.repository.ContentReleaseRepository;
import net.therap.app.repository.ContentRepository;
import net.therap.app.repository.CourseRepository;
import net.therap.app.repository.ModuleRepository;
import net.therap.app.exception.AccessDeniedException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static java.util.Objects.isNull;

/**
 * @author gazizafor
 * @since 13/8/25
 */
@Service
public class AuthorizationService {
    
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final ContentRepository contentRepository;
    private final ContentReleaseRepository contentReleaseRepository;
    private final MessageSource messageSource;
    
    // Assuming a service for student enrollment checks
    // private final EnrollmentService enrollmentService;
    
    public AuthorizationService(CourseRepository courseRepository, ModuleRepository moduleRepository,
                                ContentRepository contentRepository, ContentReleaseRepository contentReleaseRepository, MessageSource messageSource) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.contentRepository = contentRepository;
        this.contentReleaseRepository = contentReleaseRepository;
        this.messageSource = messageSource;
    }
    
    // Existing owner check methods
    private boolean isCourseOwner(long courseId, String instructorEmail) {
        return courseRepository.existsByIdAndInstructorEmail(courseId, instructorEmail);
    }
    
    private boolean isModuleOwner(long moduleId, String instructorEmail) {
        return moduleRepository.existsByIdAndCourseInstructorEmail(moduleId, instructorEmail);
    }
    
    private boolean isContentOwner(long contentId, String instructorEmail) {
        return contentRepository.existsByIdAndModuleCourseInstructorEmail(contentId, instructorEmail);
    }
    
    private boolean isContentReleaseOwner(long contentReleaseId, String instructorEmail) {
        return contentReleaseRepository.existsByIdAndContentModuleCourseInstructorEmail(contentReleaseId, instructorEmail);
    }
    
    private boolean isEnrolled(long courseId, String studentEmail) {
        return true; // Placeholder
    }
    
    public void authorize(AuthorizationLevel requiredLevel, Object resource, String userEmail, String userRole) {
        if (isNull(requiredLevel)) {
            throw new RuntimeException();
        }
        
        if (AuthorizationLevel.ADMIN.hasRole(userRole)) {
            return;
        }
        
        switch (requiredLevel) {
            case OWNER:
                if (!AuthorizationLevel.OWNER.hasRole(userRole)) {
                    throw new AccessDeniedException("Access Denied: Owner role required.");
                }
                
                if (resource instanceof Course course && !isCourseOwner(course.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.course", null, Locale.getDefault()));
                    
                } else if (resource instanceof Module module && !isModuleOwner(module.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.module", null, Locale.getDefault()));
                    
                } else if (resource instanceof Content content && !isContentOwner(content.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.content", null, Locale.getDefault()));
                    
                } else if (resource instanceof ContentRelease release && !isContentReleaseOwner(release.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.contentRelease", null, Locale.getDefault()));
                }
                
                break;
            
            case INSTRUCTOR:
                if (!AuthorizationLevel.INSTRUCTOR.hasRole(userRole)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.instructor", null, Locale.getDefault()));
                }
                
                break;
            
            case STUDENT_ENROLLED:
                if (!AuthorizationLevel.STUDENT_ENROLLED.hasRole(userRole)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.student.enrolled", null, Locale.getDefault()));
                }
                
                if (resource instanceof Course course && !isEnrolled(course.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.student.enrolled", null, Locale.getDefault()));
                }
                
                if (resource instanceof ContentRelease release && !isContentReleaseOwner(release.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.student.enrolled", null, Locale.getDefault()));
                }
                
                break;
            
            case STUDENT:
                if (!AuthorizationLevel.STUDENT.hasRole(userRole)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.student", null, Locale.getDefault()));
                }

                break;
                
            case PUBLIC:
                if (!AuthorizationLevel.STUDENT.hasRole(userRole) || !AuthorizationLevel.STUDENT.hasRole(userRole)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.public", null, Locale.getDefault()));
                }
            
            default:
                throw new RuntimeException();
        }
    }
}