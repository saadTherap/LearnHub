package net.therap.learningProcessor.service;

import net.therap.learningProcessor.dto.CourseCatalogDto;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.dto.content.ContentDetailDto;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
public interface CourseService {

    List<CourseCatalogDto> getCourseCatalogs();

    CourseCatalogDto getCourseCatalogByID(long courseId);

    CourseDetailWithProgressDto getCourseDetail(long courseId);

    List<BaseContentDto> getContentsByModuleId(long moduleId);

    ContentDetailDto getContentDetail(long contentId);
}