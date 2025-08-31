package net.therap.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.*;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.CourseMapper;
import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import net.therap.app.model.Module;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.service.*;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import net.therap.cache.support.HazelcastCacheService;
import org.apache.coyote.BadRequestException;
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
@Slf4j
@RestController
@RequestMapping("/courses")
public class CourseController {
    
    private final CourseMapper courseMapper;
    private final CourseService courseService;
    private final DtoHelper dtoHelper;
    private final HazelcastCacheService hazelcastCacheService;
    private final AuthorizationService authorizationService;
    private final MessageSource messageSource;
    private final InstructorService instructorService;
    private final ContentService contentService;
    private final ModuleService moduleService;
    
    public CourseController(CourseMapper courseMapper, CourseService courseService, DtoHelper dtoHelper,
                            HazelcastCacheService hazelcastCacheService, AuthorizationService authorizationService, MessageSource messageSource, InstructorService instructorService, ContentService contentService, ModuleService moduleService) {
        this.courseMapper = courseMapper;
        this.courseService = courseService;
        this.dtoHelper = dtoHelper;
        this.hazelcastCacheService = hazelcastCacheService;
        this.authorizationService = authorizationService;
        this.messageSource = messageSource;
        this.instructorService = instructorService;
        this.contentService = contentService;
        this.moduleService = moduleService;
    }
    
    @GetMapping
    public ResponseEntity<List<CourseCatalogDTO>> getAllCourses(HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /courses");
        authorizationService.authorize(AuthorizationLevel.ADMIN, null, request);
        List<Course> courses = courseService.findAll();
        List<CourseCatalogDTO> courseDTOs =
                courses.stream().map(dtoHelper::toDetailedCourseCatalogDTO).collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseCatalogDTO> getCourseById(@PathVariable long id, HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /courses/{} ", id);
        CourseCatalogDTO cached = hazelcastCacheService.get(CacheConstants.COURSE_CATALOG, id);
        
        if (cached != null) {
            authorizationService.authorize(AuthorizationLevel.STUDENT_ENROLLED, cached, request);
            return ResponseEntity.ok(cached);
        }
        
        Optional<Course> courseOptional = courseService.findById(id);
        
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            authorizationService.authorize(AuthorizationLevel.STUDENT_ENROLLED, course, request);
            log.info("course modules size: {}", course.getModules().size());
            CourseCatalogDTO dto = dtoHelper.toDetailedCourseCatalogDTO(course);
            hazelcastCacheService.put(CacheConstants.COURSE_CATALOG, course.getId(), dto);
            
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<CourseDTO> getCourseByIdDetails(@PathVariable long id, HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /courses/{}/details", id);
        CourseDTO cached = hazelcastCacheService.get(CacheConstants.COURSES, id);
        if (cached != null) {
            authorizationService.authorize(AuthorizationLevel.OWNER, cached, request);
            return ResponseEntity.ok(cached);
        }
        
        Optional<Course> courseOptional = courseService.findById(id);
        
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            authorizationService.authorize(AuthorizationLevel.OWNER, course, request);
            CourseDTO dto = dtoHelper.toCourseDTO(course);
            hazelcastCacheService.put(CacheConstants.COURSES, course.getId(), dto);
            
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{courseId}/versions")
    public ResponseEntity<CourseDTO> getAllCourseVersions(@PathVariable long courseId, HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /courses/{}/versions", courseId);
        Optional<Course> courseOptional = courseService.findById(courseId);
        
        if (courseOptional.isPresent()) {
            authorizationService.authorize(AuthorizationLevel.OWNER, courseOptional.get(), request);
            Course course = courseOptional.get();
            CourseDTO dto = dtoHelper.toCourseDTO(course);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/versions/{releaseNum}")
    public ResponseEntity<CourseCatalogDTO> getCourseVersionById(@PathVariable long id,
                                                          @PathVariable int releaseNum,
                                                         HttpServletRequest request) throws BadRequestException {
        
        Optional<Course> courseOptional = courseService.findById(id);
        
        if (courseOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.course", null, Locale.getDefault()));
        }
        
        authorizationService.authorize(AuthorizationLevel.OWNER, courseOptional.get(), request);

        return ResponseEntity.ok(courseService.findSpecificVersion(id,releaseNum));
    }
    
    @GetMapping("/byInstructor/{instructorId}")
    public ResponseEntity<List<CourseDTO>> getCourseByInstructorId(@PathVariable long instructorId, HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /courses/byInstructor/{}", instructorId);
        
        Optional<Instructor> instructorOptional = instructorService.getInstructorById(instructorId);
        
        if (instructorOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.instructor", null, Locale.getDefault()));
        }
        
        authorizationService.authorize(AuthorizationLevel.OWNER, instructorOptional.get(), request);
        List<Course> courseList = courseService.findByInstructor(instructorId);
        
        return new ResponseEntity<>(courseList.stream().map(dtoHelper::toCourseDTO).toList(), HttpStatus.OK);
    }
    
    @GetMapping("/byInstructor")
    public ResponseEntity<List<CourseDTO>> getCourseByInstructor(HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /courses/byInstructor");
        authorizationService.authorize(AuthorizationLevel.INSTRUCTOR, null, request);
        long instructorId = authorizationService.getInstructorIdFromRequest(request);
        List<Course> courseList = courseService.findByInstructor(instructorId);
        
        return new ResponseEntity<>(courseList.stream().map(dtoHelper::toCourseDTO).toList(), HttpStatus.OK);
    }
    
    @GetMapping("/draft")
    public ResponseEntity<List<CourseDTO>> getAllDraftCourses(HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /courses/draft");
        authorizationService.authorize(AuthorizationLevel.INSTRUCTOR, null, request);
        long instructorId = authorizationService.getInstructorIdFromRequest(request);
        List<Course> courses = courseService.findAllDrafts(instructorId);
        
        return ResponseEntity.ok(courses.stream().map(dtoHelper::toCourseDTO).collect(Collectors.toList()));
    }
    
    @GetMapping("/draft/{courseId}")
    public ResponseEntity<CourseDTO> getSpecificVersionOfCourse(@PathVariable long courseId, HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /courses/draft/{}", courseId);
        Optional<Course> courseOptional = courseService.findDraftById(courseId);
        
        if (courseOptional.isPresent()) {
            authorizationService.authorize(AuthorizationLevel.OWNER, courseOptional.get(), request);
            return ResponseEntity.ok(dtoHelper.toCourseDTO(courseOptional.get()));
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.draft", null, Locale.getDefault()));
    }
    
    @PostMapping("/draft")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody @Validated(OnCreate.class) CourseDTO courseDTO, HttpServletRequest request) throws BadRequestException {
        log.info("[POST] /courses/draft\nBody: \n{}", courseDTO);
        authorizationService.authorize(AuthorizationLevel.INSTRUCTOR, null, request);
        
        courseDTO.setInstructorId(authorizationService.getInstructorIdFromRequest(request));
        Course course = courseMapper.toCourse(courseDTO);
        course.setModules(new ArrayList<>());
        course.setCurrentRelease(0L);
        Course savedCourse = courseService.save(course);
        
        return new ResponseEntity<>(dtoHelper.toCourseDTO(savedCourse), HttpStatus.CREATED);
    }
    
    @PostMapping("/modules/reorder")
    public ResponseEntity<List<ModuleDTO>> reorderModules(@RequestBody @Validated(OnUpdate.class) List<ReorderDTO> modules, HttpServletRequest request) throws BadRequestException {
        log.info("[POST] /courses/modules/reorder\nRequestBody:\n{}", modules);
        
        if (!isValidOrderedList(modules)) {
            throw new BadRequestException(messageSource.getMessage("invalid.reorder", null, Locale.getDefault()));
        }
        
        Optional<Module> moduleOptional = moduleService.findById(modules.getFirst().getId());
        
        if (moduleOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.module", null, Locale.getDefault()));
        }
        
        authorizationService.authorize(AuthorizationLevel.OWNER, moduleOptional.get(), request);
        
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
                                                         @RequestBody @Validated(OnUpdate.class) CourseDTO courseDTO,
                                                         HttpServletRequest request) throws BadRequestException {
        log.info("[PATCH] /courses/{}\nRequestBody:\n{}", id, courseDTO);
        Optional<Course> courseToUpdate = courseService.findById(id);
        
        if (courseToUpdate.isPresent()) {
            authorizationService.authorize(AuthorizationLevel.OWNER, courseToUpdate.get(), request);
            if (courseDTO.getId() != id && courseDTO.getId() != 0) {
                return ResponseEntity.badRequest().build();
            }
            
            courseDTO.setId(courseToUpdate.get().getId());
            courseMapper.updateCourseFromCourseDTO(courseDTO, courseToUpdate.get());
            Course updatedCourse = courseService.save(courseToUpdate.get());
            
            return new ResponseEntity<>(dtoHelper.toCourseDTO(updatedCourse), HttpStatus.OK);
            
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.course", null, Locale.getDefault()));
    }
    
    @PostMapping("/publish/{id}")
    public ResponseEntity<CourseDTO> publishCourse(@PathVariable long id, HttpServletRequest request) throws BadRequestException {
        log.info("[POST] /courses/publish/{}", id);
        Optional<Course> courseOptional = courseService.findById(id);
        
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            authorizationService.authorize(AuthorizationLevel.OWNER, course, request);
            
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
            contentService.publishContentsOfCourse(course);
            Course savedCourse = courseService.save(course);
            
            return new ResponseEntity<>(dtoHelper.toCourseDTO(savedCourse), HttpStatus.OK);
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.course", null, Locale.getDefault()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable long id, HttpServletRequest request) throws BadRequestException {
        log.info("[DELETE] /courses/{}", id);
        Optional<Course> courseOptional = courseService.findById(id);
        
        if (courseOptional.isPresent()) {
            authorizationService.authorize(AuthorizationLevel.OWNER, courseOptional.get(), request);
            courseService.deleteById(id);
            
            return ResponseEntity.noContent().build();
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.course", null, Locale.getDefault()));
    }
    
    private boolean isPublishable(Course course) {
        return courseService.isPublishable(course);
    }
}