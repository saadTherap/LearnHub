package net.therap.app.controller;

import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.CourseCatalogDTO;
import net.therap.app.dto.CourseDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import net.therap.app.service.CourseService;
import net.therap.app.service.HazelcastCacheService;
import net.therap.app.service.InstructorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
    private final Logger logger = LoggerFactory.getLogger(CourseController.class);
    
    private final CourseService courseService;
    private final DtoHelper dtoHelper; // Inject DtoHelper
    private final InstructorService instructorService; // Needed to fetch Instructor entity for Course creation/update
    private final HazelcastCacheService hazelcastCacheService;
    
    public CourseController(CourseService courseService,
                            DtoHelper dtoHelper,
                            InstructorService instructorService, HazelcastCacheService hazelcastCacheService) {
        this.courseService = courseService;
        this.dtoHelper = dtoHelper;
        this.instructorService = instructorService;
        this.hazelcastCacheService = hazelcastCacheService;
    }
    
    @GetMapping
    public ResponseEntity<List<CourseCatalogDTO>> getAllCourses() {
        List<Course> courses = courseService.findAll();
        List<CourseCatalogDTO> courseDTOs = courses.stream()
                .map(dtoHelper::toDetailedCourseCatalogDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }
    
    @GetMapping("/public")
    public ResponseEntity<List<CourseCatalogDTO>> getAllCoursesCatalog() {
        List<Course> courses = courseService.findAll();
        List<CourseCatalogDTO> courseCatalogDTOs = courses.stream()
                .filter(course -> course.getCurrentRelease() > 0)
                .map(dtoHelper::toCourseCatalogDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseCatalogDTOs);
    }
    
    @GetMapping("/public/{id}")
    public ResponseEntity<CourseCatalogDTO> getCourseByIdPublic(@PathVariable Long id) {
        Optional<Course> courseOptional = courseService.findById(id);
        return courseOptional
                .filter(course -> course.getCurrentRelease() > 0)
                .map(dtoHelper::toCourseCatalogDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        Course cached = hazelcastCacheService.get(CacheConstants.COURSES, id);
        if (cached != null) {
            return ResponseEntity.ok(dtoHelper.toCourseDTO(cached));
        }

        Optional<Course> courseOptional = courseService.findById(id);

        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            hazelcastCacheService.put("courses", course.getId(), course); // Add to cache
            return ResponseEntity.ok(dtoHelper.toCourseDTO(course));
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        logger.debug("Creating course {}", courseDTO);
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course, "instructorId", "instructorName", "modules"); // Exclude relational fields
        
        // Fetch and set the Instructor entity based on instructorId from DTO
        if (courseDTO.getInstructorId() != 0) {
            Instructor instructor = instructorService.getInstructorById(courseDTO.getInstructorId())
                    .orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + courseDTO.getInstructorId()));
            course.setInstructor(instructor);
        } else {
            // Handle case where instructorId is not provided (e.g., throw error or assign default)
            return ResponseEntity.badRequest().body(null); // Or a more specific error DTO
        }
        
        Course savedCourse = courseService.save(course);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                hazelcastCacheService.remove(CacheConstants.COURSES, savedCourse.getId());
            }
        });

        return new ResponseEntity<>(dtoHelper.toCourseDTO(savedCourse), HttpStatus.CREATED); // <<< CHANGED: Use DtoHelper
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        Optional<Course> courseOptional = courseService.findById(id);
        if (courseOptional.isPresent()) {
            Course existingCourse = courseOptional.get();
            
            // --- DTO to Entity Mapping for Update ---
            // Update only allowed fields from DTO to existing entity
            existingCourse.setName(courseDTO.getName());
            existingCourse.setDescription(courseDTO.getDescription());
            existingCourse.setCurrentRelease(courseDTO.getCurrentRelease());
            
            // If instructor can be changed, fetch and set new instructor
            if (courseDTO.getInstructorId() != 0 &&
                    (existingCourse.getInstructor() == null || existingCourse.getInstructor().getId() == (courseDTO.getInstructorId()))) {
                Instructor newInstructor = instructorService.getInstructorById(courseDTO.getInstructorId())
                        .orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + courseDTO.getInstructorId()));
                existingCourse.setInstructor(newInstructor);
            }
            
            Course updatedCourse = courseService.save(existingCourse);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    hazelcastCacheService.remove(CacheConstants.COURSES, updatedCourse.getId());
                }
            });

            return ResponseEntity.ok(dtoHelper.toCourseDTO(updatedCourse)); // <<< CHANGED: Use DtoHelper
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (courseService.findById(id).isPresent()) {
            courseService.deleteById(id);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    hazelcastCacheService.remove(CacheConstants.COURSES, id);
                }
            });

            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}