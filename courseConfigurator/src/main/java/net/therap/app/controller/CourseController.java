package net.therap.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.*;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.CourseMapper;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.service.CourseService;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import net.therap.cache.support.HazelcastCacheService;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static net.therap.app.util.CollectionUtil.isValidOrderedList;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@RestController
@RequestMapping("/courses")
public class CourseController {
    
    private final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseMapper courseMapper;
    private final CourseService courseService;
    private final DtoHelper dtoHelper;
    private final HazelcastCacheService hazelcastCacheService;
    private final MessageSource messageSource;
    
    public CourseController(CourseMapper courseMapper, CourseService courseService, DtoHelper dtoHelper,
                            HazelcastCacheService hazelcastCacheService, MessageSource messageSource) {
        this.courseMapper = courseMapper;
        this.courseService = courseService;
        this.dtoHelper = dtoHelper;
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
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseCatalogDTO> getCourseById(@PathVariable long id) {
        
        CourseCatalogDTO cached = hazelcastCacheService.get(CacheConstants.COURSE_CATALOG, id);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }
        
        Optional<Course> courseOptional = courseService.findById(id);
        
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            CourseCatalogDTO dto = dtoHelper.toDetailedCourseCatalogDTO(course);
            hazelcastCacheService.put(CacheConstants.COURSE_CATALOG, course.getId(), dto);
            
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<CourseDTO> getCourseByIdDetails(@PathVariable long id) {
        
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
    
    @GetMapping("/{courseId}/versions")
    public ResponseEntity<CourseDTO> getAllCourseVersions(@PathVariable long courseId) {
        Optional<Course> courseOptional = courseService.findById(courseId);
        
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            CourseDTO dto = dtoHelper.toCourseDTO(course);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/versions/{releaseNum}")
    public ResponseEntity<CourseCatalogDTO> getCourseVersionById(@PathVariable long id,
                                                          @PathVariable int releaseNum) {
        
        return ResponseEntity.ok(courseService.findSpecificVersion(id,releaseNum));
    }
    
    @GetMapping("/byInstructor/{instructorId}")
    public ResponseEntity<List<CourseDTO>> getCourseByInstructorId(@PathVariable long instructorId) {
        List<Course> courseList = courseService.findByInstructor(instructorId);
        
        return new ResponseEntity<>(courseList.stream().map(dtoHelper::toCourseDTO).toList(), HttpStatus.OK);
    }
    
    @GetMapping("/byInstructor/{instructorId}/public")
    public ResponseEntity<List<CourseCatalogDTO>> getCourseByInstructorIdPublic(@PathVariable long instructorId) {
        List<Course> courseList = courseService.findByInstructor(instructorId);
        
        return new ResponseEntity<>(courseList.stream().map(dtoHelper::toCourseCatalogDTO).toList(), HttpStatus.OK);
    }
    
    // filter by instructor id after auth is done
    @GetMapping("/draft")
    public ResponseEntity<List<CourseDTO>> getAllDraftCourses() {
        List<Course> courses = courseService.findAllDrafts();
        
        return ResponseEntity.ok(courses.stream().map(dtoHelper::toCourseDTO).collect(Collectors.toList()));
    }
    
    @GetMapping("/draft/{courseId}")
    public ResponseEntity<CourseDTO> getSpecificVersionOfCourse(@PathVariable long courseId) {
        Optional<Course> courseOptional = courseService.findDraftById(courseId);
        
        if (courseOptional.isPresent()) {
            return ResponseEntity.ok(dtoHelper.toCourseDTO(courseOptional.get()));
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.draft", null, Locale.getDefault()));
    }
    
    @PostMapping("/draft")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody @Validated(OnCreate.class) CourseDTO courseDTO) {
        logger.debug("Creating course {}", courseDTO);
        Course course = courseMapper.toCourse(courseDTO);
        
        course.setCurrentRelease(0L);
        
        Course savedCourse = courseService.save(course);
        
        return new ResponseEntity<>(dtoHelper.toCourseDTO(savedCourse), HttpStatus.CREATED);
    }
    
    @PostMapping("/modules/reorder")
    public ResponseEntity<List<ModuleDTO>> reorderModules(@RequestBody @Validated(OnUpdate.class) List<ReorderDTO> modules) throws BadRequestException {
        if (!isValidOrderedList(modules)) {
            throw new BadRequestException(messageSource.getMessage("invalid.reorder", null, Locale.getDefault()));
        }
        
        List<ReorderDTO> sortedModules =
                modules.stream().sorted(Comparator.comparingLong(ReorderDTO::getOrderIndex)).toList();
        
        long newOrderIndex = 1;
        for (ReorderDTO module : sortedModules) {
            module.setOrderIndex(newOrderIndex++);
        }
        
        List<Module> updatedModules = courseService.reorderModules(sortedModules);
        
        return ResponseEntity.ok(updatedModules.stream().map(dtoHelper::toModuleDtoLazy).toList());
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
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, messageSource.getMessage(
                        "error.course.republish", null, request.getLocale()), request.getRequestURI());
                return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            if (!isPublishable(course)) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, messageSource.getMessage(
                        "error.course.not.publishable", null, request.getLocale()), request.getRequestURI());
                return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            course.setCurrentRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
            Course savedCourse = courseService.save(course);
            
            return new ResponseEntity<>(dtoHelper.toCourseDTO(savedCourse), HttpStatus.OK);
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
    
    private boolean isPublishable(Course course) {
        return courseService.isPublishable(course);
    }
}