package net.therap.learningProcessor.validator;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.client.CourseClient;
import net.therap.learningProcessor.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * @author avidewan
 * @since 8/3/25
 */
@Component
@RequiredArgsConstructor
public class CourseValidator {

    private final CourseClient courseClient;

    public void validateCourseExists(Long courseId) {
        wrapFeignCall(() -> courseClient.getCourseCatalog(courseId),
                "error.course.notFound",
                courseId);
    }

    public void validateContentExists(Long contentId) {
        wrapFeignCall(() -> courseClient.getContentDetail(contentId),
                "error.content.notFound",
                contentId);
    }

    private <T> T wrapFeignCall(Supplier<T> feignCall,
                                String messageKey,
                                Object... args) {
        try {
            return feignCall.get();

        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(messageKey, args);
        }
    }
}