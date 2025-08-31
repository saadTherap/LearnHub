package net.therap.app.controller;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author gazizafor
 * @since 27/8/25
 */
@ExtendWith(MockitoExtension.class) // Use MockitoExtension for @Mock and @InjectMocks
class PublicControllerIntegrationTest {
    
    private MockMvc mockMvc;
    
    @Mock private HazelcastCacheService hazelcastCacheService;
    @Mock private CourseService courseService;
    @Mock private DtoHelper dtoHelper;
    @Mock private InstructorService instructorService;
    @Mock private InstructorMapper instructorMapper;
    @Mock private MessageSource messageSource;
    
    @InjectMocks // Inject mocks into the PublicController instance
    private PublicController publicController;
    
    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(publicController)
                .setControllerAdvice(new net.therap.app.exception.GlobalExceptionHandler(messageSource))
                .build();
    }
    
    @Test
    void getAllCoursesCatalog_shouldReturnListOfCourseCatalogDTOs() throws Exception {
        // Arrange
        Course course = new Course();
        course.setId(1L);
        course.setName("Published Course");
        course.setCurrentRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
        CourseCatalogDTO dto = new CourseCatalogDTO();
        dto.setId(1L);
        dto.setName("Published Course DTO");
        
        List<Course> courses = Collections.singletonList(course);
        List<CourseCatalogDTO> dtoList = Collections.singletonList(dto);
        
        when(courseService.findAll()).thenReturn(courses);
        when(dtoHelper.toCourseCatalogDTO(any(Course.class))).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(get("/public/courses"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Published Course DTO"));
    }
    
    @Test
    void getCourseByIdPublic_courseFoundInCache_shouldReturnCachedDTO() throws Exception {
        // Arrange
        long courseId = 1L;
        CourseCatalogDTO cachedDto = new CourseCatalogDTO();
        cachedDto.setId(courseId);
        cachedDto.setName("Cached Course");
        
        when(hazelcastCacheService.get(eq(CacheConstants.COURSE_CATALOG_PUBLIC), eq(courseId))).thenReturn(cachedDto);
        
        // Act & Assert
        mockMvc.perform(get("/public/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.name").value("Cached Course"));
    }
    
    @Test
    void getCourseByIdPublic_courseFoundInDbAndPublished_shouldReturnDTO() throws Exception {
        // Arrange
        long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setCurrentRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
        CourseCatalogDTO dto = new CourseCatalogDTO();
        dto.setId(courseId);
        dto.setName("DB Course");
        
        when(hazelcastCacheService.get(eq(CacheConstants.COURSE_CATALOG_PUBLIC), eq(courseId))).thenReturn(null);
        when(courseService.findById(courseId)).thenReturn(Optional.of(course));
        when(dtoHelper.toCourseCatalogDTO(any(Course.class))).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(get("/public/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.name").value("DB Course"));
    }
    
    @Test
    void getAllInstructorsPublic_shouldReturnListOfInstructorDtoCatalogs() throws Exception {
        // Arrange
        Instructor instructor = new Instructor();
        instructor.setId(1L);
        instructor.setName("John Doe");
        InstructorDtoCatalog dto = new InstructorDtoCatalog();
        dto.setId(1L);
        dto.setName("John Doe DTO");
        
        List<Instructor> instructors = Collections.singletonList(instructor);
        List<InstructorDtoCatalog> dtoList = Collections.singletonList(dto);
        
        when(instructorService.getAllInstructors()).thenReturn(instructors);
        when(instructorMapper.toInstructorDtoCatalog(any(Instructor.class))).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(get("/public/instructors"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe DTO"));
    }
    
    @Test
    void getInstructorByIdPublic_instructorFoundInCache_shouldReturnCachedDTO() throws Exception {
        // Arrange
        long instructorId = 1L;
        InstructorDtoCatalog cachedDto = new InstructorDtoCatalog();
        cachedDto.setId(instructorId);
        cachedDto.setName("Cached Instructor");
        
        when(hazelcastCacheService.get(eq(CacheConstants.INSTRUCTOR_CATALOG_PUBLIC), eq(instructorId))).thenReturn(cachedDto);
        
        // Act & Assert
        mockMvc.perform(get("/public/instructors/{id}", instructorId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(instructorId))
                .andExpect(jsonPath("$.name").value("Cached Instructor"));
    }
    
    @Test
    void getInstructorByIdPublic_instructorFoundInDb_shouldReturnDTO() throws Exception {
        // Arrange
        long instructorId = 1L;
        Instructor instructor = new Instructor();
        instructor.setId(instructorId);
        instructor.setName("DB Instructor");
        InstructorDtoCatalog dto = new InstructorDtoCatalog();
        dto.setId(instructorId);
        dto.setName("DB Instructor DTO");
        
        when(hazelcastCacheService.get(eq(CacheConstants.INSTRUCTOR_CATALOG_PUBLIC), eq(instructorId))).thenReturn(null);
        when(instructorService.getInstructorById(instructorId)).thenReturn(Optional.of(instructor));
        when(instructorMapper.toInstructorDtoCatalog(any(Instructor.class))).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(get("/public/instructors/{id}", instructorId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(instructorId))
                .andExpect(jsonPath("$.name").value("DB Instructor DTO"));
    }
    
    @Test
    void getCourseByInstructorIdPublic_shouldReturnListOfCourseCatalogDTOs() throws Exception {
        // Arrange
        long instructorId = 1L;
        Course course = new Course();
        course.setId(10L);
        course.setName("Instructor's Course");
        course.setCurrentRelease(ReleaseStatus.INITIAL_PUBLISHED.getReleaseNumber());
        CourseCatalogDTO dto = new CourseCatalogDTO();
        dto.setId(10L);
        dto.setName("Instructor's Course DTO");
        
        List<Course> courseList = Collections.singletonList(course);
        List<CourseCatalogDTO> dtoList = Collections.singletonList(dto);
        
        when(courseService.findByInstructor(instructorId)).thenReturn(courseList);
        when(dtoHelper.toCourseCatalogDTO(any(Course.class))).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(get("/public/courses/byInstructor/{instructorId}", instructorId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].name").value("Instructor's Course DTO"));
    }
    
    @Test
    void getInstructorByIdPublic_instructorNotFound_shouldReturnNotFoundStatus() throws Exception {
        // Arrange
        long instructorId = 1L;
        when(hazelcastCacheService.get(eq(CacheConstants.INSTRUCTOR_CATALOG_PUBLIC), eq(instructorId))).thenReturn(null);
        when(instructorService.getInstructorById(instructorId)).thenReturn(Optional.empty());
        // Mock the messageSource call that the controller makes before throwing NoSuchElementException
        when(messageSource.getMessage(eq("not.found.instructor"), any(), any(Locale.class))).thenReturn("Instructor not found");
        
        // Act & Assert
        mockMvc.perform(get("/public/instructors/{id}", instructorId))
                .andExpect(status().isNotFound()) // Expect a 404 Not Found status
                .andExpect(jsonPath("$.message").value("Instructor not found")); // Verify the error message
    }
    
    @Test
    void getCourseByIdPublic_courseNotFound_shouldReturnNotFoundStatus() throws Exception {
        // Arrange
        long courseId = 1L;
        when(hazelcastCacheService.get(eq(CacheConstants.COURSE_CATALOG_PUBLIC), eq(courseId))).thenReturn(null);
        when(courseService.findById(courseId)).thenReturn(Optional.empty());
        // Mock the messageSource call that the controller makes before throwing NoSuchElementException
        when(messageSource.getMessage(eq("not.found.course"), any(), any(Locale.class))).thenReturn("Course not found");
        
        // Act & Assert
        mockMvc.perform(get("/public/courses/{id}", courseId))
                .andExpect(status().isNotFound()) // Expect a 404 Not Found status
                .andExpect(jsonPath("$.message").value("Course not found")); // Verify the error message
    }
    
}