package net.therap.app.controller;

import lombok.extern.slf4j.Slf4j;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.CourseCatalogDTO;
import net.therap.app.dto.InstructorDtoCatalog;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.InstructorMapper;
import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.service.CourseService;
import net.therap.app.service.InstructorService;
import net.therap.cache.support.HazelcastCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gazizafor
 * @since 11/8/25
 */
@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {
    
    private final HazelcastCacheService hazelcastCacheService;
    private final CourseService courseService;
    private final DtoHelper dtoHelper;
    private final InstructorService instructorService;
    private final InstructorMapper instructorMapper;
    
    public PublicController(HazelcastCacheService hazelcastCacheService, CourseService courseService,
                            DtoHelper dtoHelper, InstructorService instructorService,
                            InstructorMapper instructorMapper) {
        this.hazelcastCacheService = hazelcastCacheService;
        this.courseService = courseService;
        this.dtoHelper = dtoHelper;
        this.instructorService = instructorService;
        this.instructorMapper = instructorMapper;
    }
    
    @GetMapping("/courses")
    public ResponseEntity<List<CourseCatalogDTO>> getAllCoursesCatalog() {
        log.info("[GET] /public/courses");
        List<Course> courses = courseService.findAll();
        List<CourseCatalogDTO> courseCatalogDTOs =
                courses.stream().filter(course -> course.getCurrentRelease() > ReleaseStatus.DRAFT.getReleaseNumber()).map(dtoHelper::toCourseCatalogDTO).collect(Collectors.toList());
        return ResponseEntity.ok(courseCatalogDTOs);
    }
    
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseCatalogDTO> getCourseByIdPublic(@PathVariable Long id) {
        log.info("[GET] /public/courses/{}",id);
        CourseCatalogDTO cached = hazelcastCacheService.get(CacheConstants.COURSE_CATALOG_PUBLIC, id);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }
        
        Optional<Course> courseOptional = courseService.findById(id);
        
        if (courseOptional.isPresent() && courseOptional.get().getCurrentRelease() > ReleaseStatus.DRAFT.getReleaseNumber()) {
            CourseCatalogDTO dto = dtoHelper.toCourseCatalogDTO(courseOptional.get());
            hazelcastCacheService.put(CacheConstants.COURSE_CATALOG_PUBLIC, id, dto);
            return ResponseEntity.ok(dto);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/instructors")
    public ResponseEntity<List<InstructorDtoCatalog>> getAllInstructorsPublic() {
        log.info("[GET] /public/instructors");
        List<Instructor> instructors = instructorService.getAllInstructors();
        List<InstructorDtoCatalog> instructorDTOs =
                instructors.stream().map(instructorMapper::toInstructorDtoCatalog).collect(Collectors.toList());
        
        return ResponseEntity.ok(instructorDTOs);
    }
    
    @GetMapping("/instructors/{id}")
    public ResponseEntity<InstructorDtoCatalog> getInstructorByIdPublic(@PathVariable long id) {
        log.info("[GET] /public/instructors/{}", id);
        InstructorDtoCatalog cached = hazelcastCacheService.get(CacheConstants.INSTRUCTOR_CATALOG_PUBLIC, id);
        
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }
        
        return instructorService.getInstructorById(id).map(instructor -> {
            InstructorDtoCatalog dto = instructorMapper.toInstructorDtoCatalog(instructor);
            hazelcastCacheService.put(CacheConstants.INSTRUCTOR_CATALOG_PUBLIC, id, dto);
            
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/courses/byInstructor/{instructorId}")
    public ResponseEntity<List<CourseCatalogDTO>> getCourseByInstructorIdPublic(@PathVariable long instructorId) {
        log.info("[GET] /public/courses/byInstructor/{}", instructorId);
        List<Course> courseList = courseService.findByInstructor(instructorId);
        
        return new ResponseEntity<>(courseList.stream().map(dtoHelper::toCourseCatalogDTO).toList(), HttpStatus.OK);
    }
}