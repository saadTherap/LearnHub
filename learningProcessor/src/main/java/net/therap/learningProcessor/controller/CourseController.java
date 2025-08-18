package net.therap.learningProcessor.controller;

import lombok.RequiredArgsConstructor;
import net.therap.cache.support.HazelcastCacheService;
import net.therap.learningProcessor.constants.CacheConstants;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final HazelcastCacheService hazelcastCacheService;

    @GetMapping("/public")
    public List<CourseCatalogDto> getAllCourses() {
        return courseService.getCourseCatalogs();
    }

    @GetMapping("/public/{courseId}")
    public CourseCatalogDto getCourseCatalog(@PathVariable long courseId) {
        CourseCatalogDto cached = hazelcastCacheService.get(CacheConstants.COURSE_CATALOG_LP, courseId);
        if (cached != null) return cached;

        CourseCatalogDto dto = courseService.getCourseCatalogByID(courseId);
        if (dto != null) hazelcastCacheService.put(CacheConstants.COURSE_CATALOG_LP, courseId, dto);

        return dto;
    }

    @GetMapping("/{courseId}")
    public CourseDetailWithProgressDto getCourseDetail(@PathVariable long courseId) {
        return courseService.getCourseDetail(courseId);
    }

    @GetMapping("/modules/{moduleId}/contents")
    public List<BaseContentDto> getContentsByModule(@PathVariable long moduleId) {
        List<BaseContentDto> cached = hazelcastCacheService.get(CacheConstants.MODULE_CONTENTS, moduleId);
        if (cached != null) return cached;

        List<BaseContentDto> list = courseService.getContentsByModuleId(moduleId);
        if (list != null) hazelcastCacheService.put(CacheConstants.MODULE_CONTENTS, moduleId, list);

        return list;
    }

    @GetMapping("/contents/{contentId}")
    public ContentDetailDto getContentDetail(@PathVariable long contentId) {
        ContentDetailDto cached = hazelcastCacheService.get(CacheConstants.CONTENT_DETAIL, contentId);
        if (cached != null) return cached;

        ContentDetailDto dto = courseService.getContentDetail(contentId);
        if (dto != null) hazelcastCacheService.put(CacheConstants.CONTENT_DETAIL, contentId, dto);

        return dto;
    }
}