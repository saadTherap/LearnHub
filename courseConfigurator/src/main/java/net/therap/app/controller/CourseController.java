package net.therap.app.controller;

import net.therap.app.dto.CourseCatalogDTO;
import net.therap.app.dto.CourseDTO;
import net.therap.app.helper.DtoHelper; // Import DtoHelper
import net.therap.app.model.Course;
import net.therap.app.model.Instructor; // Assuming Instructor entity is needed for mapping
import net.therap.app.service.CourseService;
import net.therap.app.service.InstructorService; // Assuming InstructorService is needed to fetch Instructor
import net.therap.app.service.ModuleService; // Keep this if still used for other logic
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired; // Can replace with constructor injection
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException; // Added for clarity on exceptions
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
    private final ModuleService moduleService; // Keep if used
    private final DtoHelper dtoHelper; // Inject DtoHelper
    private final InstructorService instructorService; // Needed to fetch Instructor entity for Course creation/update
    
    // Use constructor injection for all dependencies
    public CourseController(CourseService courseService,
                            ModuleService moduleService,
                            DtoHelper dtoHelper,
                            InstructorService instructorService) {
        this.courseService = courseService;
        this.moduleService = moduleService;
        this.dtoHelper = dtoHelper;
        this.instructorService = instructorService;
    }
    
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<Course> courses = courseService.findAll();
        List<CourseDTO> courseDTOs = courses.stream()
                .map(dtoHelper::toCourseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }
    
    @GetMapping("/catalog")
    public ResponseEntity<List<CourseCatalogDTO>> getAllCoursesCatalog() {
        List<Course> courses = courseService.findAll();
        List<CourseCatalogDTO> courseCatalogDTOs = courses.stream()
                .filter(course -> course.getCurrentRelease() > 0)
                .map(dtoHelper::toCourseCatalogDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseCatalogDTOs);
    }
    
    @GetMapping("/catalog/{id}")
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
        Optional<Course> courseOptional = courseService.findById(id);
        return courseOptional
                .map(dtoHelper::toCourseDTO) // <<< CHANGED: Use DtoHelper
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
            return ResponseEntity.ok(dtoHelper.toCourseDTO(updatedCourse)); // <<< CHANGED: Use DtoHelper
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (courseService.findById(id).isPresent()) {
            courseService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}