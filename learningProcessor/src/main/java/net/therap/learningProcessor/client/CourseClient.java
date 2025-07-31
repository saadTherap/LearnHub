package net.therap.learningProcessor.client;

import net.therap.learningProcessor.dto.CourseCatalogDto;
import net.therap.learningProcessor.dto.CourseDetailDto;
import net.therap.learningProcessor.dto.ModuleDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.dto.content.ContentDetailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author avidewan
 * @since 7/27/25
 */
@FeignClient(name = "course-configurator", url = "http://172.16.2.118:8082/myApp/api")
public interface CourseClient {

    @GetMapping("/courses/public")
    List<CourseCatalogDto> getAllCourseCatalogs();

    @GetMapping("/courses/public/{courseId}")
    CourseCatalogDto getCourseCatalog(@PathVariable("courseId") Long courseId);

    @GetMapping("/courses/{courseId}")
    CourseDetailDto getCourseDetail(@PathVariable("courseId") Long courseId);

    @GetMapping("/modules/byCourse/{courseId}")
    List<ModuleDto> getModulesByCourse(@PathVariable("courseId") Long courseId);

    @GetMapping("/contents/byModule/{moduleId}")
    List<BaseContentDto> getContentsByModule(@PathVariable("moduleId") Long moduleId);

    @GetMapping("/contents/detail/{contentId}")
    ContentDetailDto getContentDetail(@PathVariable("contentId") Long contentId);
}