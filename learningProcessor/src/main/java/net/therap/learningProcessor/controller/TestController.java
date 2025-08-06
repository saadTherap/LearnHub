package net.therap.learningProcessor.controller;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.client.CourseClient;
import net.therap.learningProcessor.dto.CourseCatalogDto;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.ModuleWithProgressDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.dto.content.ContentDetailDto;
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
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final CourseClient courseClient;

    @GetMapping("/catalogs")
    public List<CourseCatalogDto> getCourseCatalog() {
        return courseClient.getAllCourseCatalogs();
    }

    @GetMapping("/catalog/{courseId}")
    public CourseCatalogDto getCourseCatalogById(@PathVariable("courseId") Long courseId) {
        return courseClient.getCourseCatalog(courseId);
    }

    @GetMapping("/courses/{courseId}")
    public CourseDetailWithProgressDto getCourseDetail(@PathVariable("courseId") Long courseId) {
        return courseClient.getCourseDetail(courseId);
    }

    @GetMapping("/modules/byCourse/{courseId}")
    public List<ModuleWithProgressDto> testModulesByCourse(@PathVariable Long courseId) {
        return courseClient.getModulesByCourse(courseId);
    }

    @GetMapping("/contents/byModule/{moduleId}")
    public List<BaseContentDto> testBaseContents(@PathVariable Long moduleId) {
        return courseClient.getContentsByModule(moduleId);
    }

    @GetMapping("/contents/detail/{contentId}")
    public ContentDetailDto testContentDetail(@PathVariable Long contentId) {
        return courseClient.getContentDetail(contentId);
    }
}