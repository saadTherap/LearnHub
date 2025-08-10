package net.therap.learningProcessor.controller;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.dto.CourseCatalogDto;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.dto.content.ContentDetailDto;
import net.therap.learningProcessor.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/public")
    public List<CourseCatalogDto> getAllCourses() {
        return courseService.getCourseCatalogs();
    }

    @GetMapping("/public/{courseId}")
    public CourseCatalogDto getCourseCatalog(@PathVariable long courseId) {
        return courseService.getCourseCatalogByID(courseId);
    }

    @GetMapping("/{courseId}")
    public CourseDetailWithProgressDto getCourseDetail(@PathVariable long courseId) {
        return courseService.getCourseDetail(courseId);
    }

    @GetMapping("/modules/{moduleId}/contents")
    public List<BaseContentDto> getContentsByModule(@PathVariable long moduleId) {
        return courseService.getContentsByModuleId(moduleId);
    }

    @GetMapping("/contents/{contentId}")
    public ContentDetailDto getContentDetail(@PathVariable long contentId) {
        return courseService.getContentDetail(contentId);
    }
}