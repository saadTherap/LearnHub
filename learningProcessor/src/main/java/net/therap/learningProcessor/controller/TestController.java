package net.therap.learningProcessor.controller;

import lombok.RequiredArgsConstructor;
import net.therap.learningProcessor.client.CourseClient;
import net.therap.learningProcessor.client.FileClient;
import net.therap.learningProcessor.dto.CourseCatalogDto;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.ModuleWithProgressDto;
import net.therap.learningProcessor.dto.StoredFileDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.dto.content.ContentDetailDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileClient fileClient;

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


    // FileClient endpoints

    @GetMapping("/files")
    public List<StoredFileDto> getAllFiles() {
        return fileClient.getAllFiles();
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        return fileClient.downloadFile(id);
    }

    @DeleteMapping("/files/{id}")
    public void deleteFile(@PathVariable Long id) {
        fileClient.deleteFile(id);
    }

    @PostMapping("/files/upload")
    public StoredFileDto uploadFile(@RequestParam("file") MultipartFile file) {
        return fileClient.uploadFile(file);
    }
}