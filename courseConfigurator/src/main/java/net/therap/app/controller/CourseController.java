package net.therap.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.CourseCatalogDTO;
import net.therap.app.dto.CourseDTO;
import net.therap.app.dto.ErrorResponse;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.CourseMapper;
import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.service.CourseService;
import net.therap.app.service.HazelcastCacheService;
import net.therap.app.service.InstructorService;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.validation.annotation.Validated;
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
    
    private final CourseMapper courseMapper;
    
    private final CourseService courseService;
    private final DtoHelper dtoHelper;
    private final InstructorService instructorService;
    private final HazelcastCacheService hazelcastCacheService;
    private final MessageSource messageSource;
    
    public CourseController(CourseMapper courseMapper, CourseService courseService, DtoHelper dtoHelper,
                            InstructorService instructorService, HazelcastCacheService hazelcastCacheService, MessageSource messageSource) {
        this.courseMapper = courseMapper;
        this.courseService = courseService;
        this.dtoHelper = dtoHelper;
        this.instructorService = instructorService;
        this.hazelcastCacheService = hazelcastCacheService;
        this.messageSource = messageSource;
    }
    
    @GetMapping
    public ResponseEntity<List<CourseCatalogDTO>> getAllCourses() {
        List<Course> courses = courseService.findAll();
        List<CourseCatalogDTO> courseDTOs =
                courses.stream().map(dtoHelper::toDetailedCourseCatalogDTO).collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }
    
    @GetMapping("/public")
    public ResponseEntity<List<CourseCatalogDTO>> getAllCoursesCatalog() {
        List<Course> courses = courseService.findAll();
        List<CourseCatalogDTO> courseCatalogDTOs =
                courses.stream().filter(course -> course.getCurrentRelease() > ReleaseStatus.DRAFT.getReleaseNumber()).map(dtoHelper::toCourseCatalogDTO).collect(Collectors.toList());
        return ResponseEntity.ok(courseCatalogDTOs);
    }
    
    @GetMapping("/public/{id}")
    public ResponseEntity<CourseCatalogDTO> getCourseByIdPublic(@PathVariable Long id) {
        Optional<Course> courseOptional = courseService.findById(id);
        return courseOptional.filter(course -> course.getCurrentRelease() > ReleaseStatus.DRAFT.getReleaseNumber()).map(dtoHelper::toCourseCatalogDTO).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO cached = hazelcastCacheService.get(CacheConstants.COURSES, id);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }
        
        Optional<Course> courseOptional = courseService.findById(id);
        
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            CourseDTO dto = dtoHelper.toCourseDTO(course);
            hazelcastCacheService.put(CacheConstants.COURSES, course.getId(), dto);
            
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody @Validated(OnCreate.class) CourseDTO courseDTO) {
        logger.debug("Creating course {}", courseDTO);
        Course course = courseMapper.toCourse(courseDTO);
        
        course.setCurrentRelease(0L); // Set an initial release number

//        BeanUtils.copyProperties(courseDTO, course, "instructorId", "instructorName", "modules"); // Exclude
//        relational fields
        
        // Fetch and set the Instructor entity based on instructorId from DTO
//        if (courseDTO.getInstructorId() != 0) {
//            Instructor instructor = instructorService.getInstructorById(courseDTO.getInstructorId())
//                    .orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + courseDTO
//                    .getInstructorId()));
//            course.setInstructor(instructor);
//        } else {
//            // Handle case where instructorId is not provided (e.g., throw error or assign default)
//            return ResponseEntity.badRequest().body(null); // Or a more specific error DTO
//        }
        
        Course savedCourse = courseService.save(course);

//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//                hazelcastCacheService.remove(CacheConstants.COURSES, savedCourse.getId());
//            }
//        });
        
        return new ResponseEntity<>(dtoHelper.toCourseDTO(savedCourse), HttpStatus.CREATED); // <<< CHANGED: Use
        // DtoHelper
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCoursePartial(@PathVariable long id,
                                                         @RequestBody @Validated(OnUpdate.class) CourseDTO courseDTO) {
        Optional<Course> courseToUpdate = courseService.findById(id);
        
        if (courseToUpdate.isPresent()) {
            if (courseDTO.getId() != id && courseDTO.getId() != 0) {
                return ResponseEntity.badRequest().build();
            }
            
            courseDTO.setId(courseToUpdate.get().getId());
            courseMapper.updateCourseFromCourseDTO(courseDTO, courseToUpdate.get());
            Course updatedCourse = courseService.save(courseToUpdate.get());
            
            return new ResponseEntity<>(dtoHelper.toCourseDTO(updatedCourse), HttpStatus.OK);
            
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/publish/{id}")
    public ResponseEntity<CourseDTO> publishCourse(@PathVariable long id, HttpServletRequest request) {
        Optional<Course> courseOptional = courseService.findById(id);
        
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            
            if (course.getCurrentRelease() > ReleaseStatus.DRAFT.getReleaseNumber()) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.course.republish", null, request.getLocale()), request.getRequestURI());
                return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            // validate publishable or not
            if (!isPublishable(course)) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, messageSource.getMessage("error.course.not.publishable", null, request.getLocale()), request.getRequestURI());
                return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            course.setCurrentRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
            Course savedCourse = courseService.save(course);
            
            return new ResponseEntity<>(dtoHelper.toCourseDTO(savedCourse), HttpStatus.OK);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    private boolean isPublishable(Course course) {
        return courseService.isPublishable(course);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable long id, @RequestBody CourseDTO courseDTO) {
        Optional<Course> courseOptional = courseService.findById(id);
        if (courseOptional.isPresent()) {
            Course existingCourse = courseOptional.get();
            
            // --- DTO to Entity Mapping for Update ---
            // Update only allowed fields from DTO to existing entity
            existingCourse.setName(courseDTO.getName());
            existingCourse.setDescription(courseDTO.getDescription());
            existingCourse.setCurrentRelease(courseDTO.getCurrentRelease());
            
            // If instructor can be changed, fetch and set new instructor
            if (courseDTO.getInstructorId() != 0 && (existingCourse.getInstructor() == null || existingCourse.getInstructor().getId() == (courseDTO.getInstructorId()))) {
                Instructor newInstructor =
                        instructorService.getInstructorById(courseDTO.getInstructorId()).orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + courseDTO.getInstructorId()));
                existingCourse.setInstructor(newInstructor);
            }
            
            Course updatedCourse = courseService.save(existingCourse);
            
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    hazelcastCacheService.remove(CacheConstants.COURSES, updatedCourse.getId());
                }
            });
            
            return ResponseEntity.ok(dtoHelper.toCourseDTO(updatedCourse));
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