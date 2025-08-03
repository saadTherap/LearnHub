package net.therap.learningProcessor.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.client.CourseClient;
import net.therap.learningProcessor.dto.CourseCatalogDto;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.dto.content.ContentDetailDto;
import net.therap.learningProcessor.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseClient courseClient;

    @Override
    public List<CourseCatalogDto> getCourseCatalogs() {
        return courseClient.getAllCourseCatalogs();
    }

    @Override
    public CourseCatalogDto getCourseCatalogByID(long courseId) {
        return wrapFeignCall(() -> courseClient.getCourseCatalog(courseId),
                "error.course.notFound",
                courseId);
    }

    @Override
    public CourseDetailWithProgressDto getCourseDetail(long courseId) {
        return wrapFeignCall(() -> courseClient.getCourseDetail(courseId),
                "error.course.notFound",
                courseId);
    }

    @Override
    public List<BaseContentDto> getContentsByModuleId(long moduleId) {
        return courseClient.getContentsByModule(moduleId);
    }

    @Override
    public ContentDetailDto getContentDetail(long contentId) {
        return wrapFeignCall(() -> courseClient.getContentDetail(contentId),
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