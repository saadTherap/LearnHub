package net.therap.app.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.therap.app.dto.CourseCatalogDTO;
import net.therap.app.dto.CourseDTO;
import net.therap.app.exception.AccessDeniedException;
import net.therap.app.model.*;
import net.therap.app.model.Module;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.repository.*;
import net.therap.auth.lib.context.UserRequestCache;
import net.therap.auth.lib.util.AuthDataUtil;
import org.apache.coyote.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * @author gazizafor
 * @since 13/8/25
 */
@Slf4j
@Service
public class AuthorizationService {
    
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final ContentRepository contentRepository;
    private final ContentReleaseRepository contentReleaseRepository;
    private final InstructorRepository instructorRepository;
    private final MessageSource messageSource;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    
    // Assuming a service for student enrollment checks
    // private final EnrollmentService enrollmentService;
    
    public AuthorizationService(CourseRepository courseRepository, ModuleRepository moduleRepository,
                                ContentRepository contentRepository, ContentReleaseRepository contentReleaseRepository, InstructorRepository instructorRepository, MessageSource messageSource, CourseEnrollmentRepository courseEnrollmentRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.contentRepository = contentRepository;
        this.contentReleaseRepository = contentReleaseRepository;
        this.instructorRepository = instructorRepository;
        this.messageSource = messageSource;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
    }
    
    private UserRequestCache.UserInfo parseUserInfoFromRequest(HttpServletRequest request) throws BadRequestException {
        long userId;
        try {
            userId = (Long) request.getAttribute("userId");
            
        } catch (Exception e) {
            log.error(messageSource.getMessage("authorization.error.invalid.user.id", null, Locale.getDefault()), e);
            
            throw new RuntimeException(messageSource.getMessage("authorization.error.invalid.user.id", null, Locale.getDefault()));
        }
        
        UserRequestCache.UserInfo userInfo = AuthDataUtil.getUserInfo(userId);

        if(userInfo == null) {
            throw new BadRequestException(messageSource.getMessage("invalid.user.id", null, Locale.getDefault()));
        }

        return userInfo;
    }
    
    public long getInstructorIdFromRequest(HttpServletRequest request) throws BadRequestException {
        UserRequestCache.UserInfo userInfo = parseUserInfoFromRequest(request);
        String email = userInfo.email();
//        String email = "instructor1@gmail.com";
        Optional<Instructor> instructorOptional = instructorRepository.findByEmail(email);
        
        if (instructorOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.instructor", null, Locale.getDefault()));
        }
        
        return instructorOptional.get().getId();
    }
    
    public void authorize(AuthorizationLevel requiredLevel, Object resource, HttpServletRequest request) throws AccessDeniedException, BadRequestException {
        UserRequestCache.UserInfo userInfo = parseUserInfoFromRequest(request);
        String userRole = userInfo.role();
        String userEmail = userInfo.email();
        
//        String userRole = "INSTRUCTOR";
//        String userEmail = "instructor1@gmail.com";
        
        if (isNull(requiredLevel)) {
            throw new RuntimeException();
        }
        
        if (AuthorizationLevel.ADMIN.hasRole(userRole)) {
            return;
        }
        
        switch (requiredLevel) {
            case OWNER:
                if (!AuthorizationLevel.OWNER.hasRole(userRole)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.owner", null, Locale.getDefault()));
                }
                
                if (resource instanceof Course course && !isCourseOwner(course.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.course", null, Locale.getDefault()));
                    
                } else if (resource instanceof Module module && !isModuleOwner(module.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.module", null, Locale.getDefault()));
                    
                } else if (resource instanceof Content content && !isContentOwner(content.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.content", null, Locale.getDefault()));
                    
                } else if (resource instanceof ContentRelease release && !isContentReleaseOwner(release.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.contentRelease", null, Locale.getDefault()));
                    
                } else if (resource instanceof Instructor instructor && !isOwnProfile(instructor, userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.owner", null, Locale.getDefault()));
                
                } else if (resource instanceof CourseCatalogDTO courseCatalogDTO && !isCourseOwner(courseCatalogDTO.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.owner", null, Locale.getDefault()));
                    
                } else if (resource instanceof CourseDTO courseDTO && !isCourseOwner(courseDTO.getId(), userEmail)) {
                    throw new AccessDeniedException(messageSource.getMessage("access.denied.owner", null, Locale.getDefault()));
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
                
                long courseId = getCourseIdFromResource(resource);
                
                if (courseId != -1 && !isEnrolled(courseId, userEmail)) {
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
                
                break;
            
            default:
                throw new RuntimeException();
        }
    }
    
    private static long getCourseIdFromResource(Object resource) {
        long courseId = -1;
        
        switch (resource) {
            case null -> {
                return courseId;
            }
            
            case Course course -> courseId = course.getId();
            
            case Module module -> courseId = module.getCourse().getId();
            
            case Content content -> courseId = content.getModule().getCourse().getId();
            
            case ContentRelease release -> courseId = release.getContent().getModule().getCourse().getId();
            
            default -> {}
        }
        
        return courseId;
    }
    
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
        return courseEnrollmentRepository.existsCourseEnrollmentByCourseIdAndStudent_Email(courseId, studentEmail);
    }
    
    
    private boolean isOwnProfile(Instructor instructor, String userEmail) {
        Optional<Instructor> instructorOptional = instructorRepository.findByEmail(userEmail);
        
        return instructorOptional.map(value -> value.equals(instructor)).orElse(false);
    }
}