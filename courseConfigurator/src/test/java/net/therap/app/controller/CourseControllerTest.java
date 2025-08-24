package net.therap.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.CourseCatalogDTO;
import net.therap.app.dto.CourseDTO;
import net.therap.app.service.AuthorizationService;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.CourseMapper;
import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.service.CourseService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author riadanonto
 * @since 13/8/25
 */
@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock private CourseMapper courseMapper;
    @Mock private CourseService courseService;
    @Mock private DtoHelper dtoHelper;
    @Mock private HazelcastCacheService hazelcastCacheService;
    @Mock private AuthorizationService authorizationService;
    @Mock private MessageSource messageSource;
    @Mock private HttpServletRequest httpServletRequest;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }

    @Test
    void testGetAllCourses() throws Exception {
        Course course = new Course();
        CourseCatalogDTO dto = new CourseCatalogDTO();

        when(courseService.findAll()).thenReturn(List.of(course));
        when(dtoHelper.toDetailedCourseCatalogDTO(any(Course.class))).thenReturn(dto);

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetCourseById_fromCache() throws Exception {
        long courseId = 1L;
        CourseCatalogDTO cachedDto = new CourseCatalogDTO();
        cachedDto.setId(courseId);

//        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.STUDENT_ENROLLED), eq(null), any(HttpServletRequest.class));

        when(hazelcastCacheService.get(CacheConstants.COURSE_CATALOG, courseId)).thenReturn(cachedDto);

        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.STUDENT_ENROLLED), eq(cachedDto), any(HttpServletRequest.class));

        mockMvc.perform(get("/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(courseId));

        verify(hazelcastCacheService).get(CacheConstants.COURSE_CATALOG, courseId);
        verify(courseService, never()).findById(courseId);
    }

    @Test
    void testGetCourseById_cacheMiss() throws Exception {
        long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setModules(new ArrayList<>());

        CourseCatalogDTO dto = new CourseCatalogDTO();
        dto.setId(courseId);

        when(hazelcastCacheService.get(CacheConstants.COURSE_CATALOG, courseId)).thenReturn(null);
        when(courseService.findById(courseId)).thenReturn(Optional.of(course));

//        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.STUDENT_ENROLLED), eq(null), any(HttpServletRequest.class));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.STUDENT_ENROLLED), eq(course), any(HttpServletRequest.class));

        doNothing().when(hazelcastCacheService).put(CacheConstants.COURSE_CATALOG, courseId, dto);
        when(dtoHelper.toDetailedCourseCatalogDTO(course)).thenReturn(dto);

        mockMvc.perform(get("/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(courseId));
    }

    @Test
    void testGetCourseById_notFound() throws Exception {
        long courseId = 1L;
        when(hazelcastCacheService.get(CacheConstants.COURSE_CATALOG, courseId)).thenReturn(null);
        when(courseService.findById(courseId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/courses/{id}", courseId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCourse_success() throws Exception {
        long courseId = 1L;
        Course course = new Course();
        course.setId(courseId); // Set a valid ID

        when(courseService.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(course), any(HttpServletRequest.class));
        // The deleteById method now returns a value, so we need to mock it
        when(courseService.deleteById(courseId)).thenReturn(course); // Assuming deleteById returns Course

        mockMvc.perform(delete("/courses/{id}", courseId))
                .andExpect(status().isNoContent());

        verify(courseService).deleteById(courseId);
    }

    @Test
    void testGetCourseByInstructor_success() throws Exception {
        // Given
        long instructorId = 1L;
        Instructor instructor = new Instructor();
        instructor.setId(instructorId);
        Course course = new Course();
        course.setInstructor(instructor);
        CourseDTO dto = new CourseDTO();

        when(authorizationService.getInstructorIdFromRequest(any(HttpServletRequest.class))).thenReturn(instructorId);
        when(courseService.findByInstructor(instructorId)).thenReturn(List.of(course));
        when(dtoHelper.toCourseDTO(any(Course.class))).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/courses/byInstructor"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}