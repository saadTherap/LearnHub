package net.therap.learningProcessor.service;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.client.CourseClient;
import net.therap.learningProcessor.dto.CourseCatalogDto;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.dto.content.ContentDetailDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseClient courseClient;

    @Override
    public List<CourseCatalogDto> getCourseCatalogs() {
        return courseClient.getAllCourseCatalogs();
    }

    @Override
    public CourseCatalogDto getCourseCatalogByID(long courseId) {
        return courseClient.getCourseCatalog(courseId);
    }

    @Override
    public CourseDetailWithProgressDto getCourseDetail(long courseId) {
        return courseClient.getCourseDetail(courseId);
    }

    @Override
    public List<BaseContentDto> getContentsByModuleId(long moduleId) {
        return courseClient.getContentsByModule(moduleId);
    }

    @Override
    public ContentDetailDto getContentDetail(long contentId) {
        return courseClient.getContentDetail(contentId);
    }
}