//package net.therap.learningProcessor.client;
//
//import net.therap.learningProcessor.dto.CourseCatalogDto;
//import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
//import net.therap.learningProcessor.dto.ModuleWithProgressDto;
//import net.therap.learningProcessor.dto.content.BaseContentDto;
//import net.therap.learningProcessor.dto.content.ContentDetailDto;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.List;
//
///**
// * @author avidewan
// * @since 7/27/25
// */
//@FeignClient(name = "course-configurator", url = "${course-configurator.url}", path = "api/course-configurator")
//public interface CourseClient {
//
//    @GetMapping("/public/courses")
//    List<CourseCatalogDto> getAllCourseCatalogs();
//
//    @GetMapping("/public/courses/{courseId}")
//    CourseCatalogDto getCourseCatalog(@PathVariable("courseId") Long courseId);
//
//    @GetMapping("/courses/{courseId}")
//    CourseDetailWithProgressDto getCourseDetail(@PathVariable("courseId") Long courseId);
//
//    @GetMapping("/modules/byCourse/{courseId}")
//    List<ModuleWithProgressDto> getModulesByCourse(@PathVariable("courseId") Long courseId);
//
//    @GetMapping("/contents/byModule/{moduleId}")
//    List<BaseContentDto> getContentsByModule(@PathVariable("moduleId") Long moduleId);
//
//    @GetMapping("/contents/detail/{contentId}")
//    ContentDetailDto getContentDetail(@PathVariable("contentId") Long contentId);
//}