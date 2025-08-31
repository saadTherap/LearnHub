package net.therap.learningProcessor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.therap.cache.support.HazelcastCacheService;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.StudentCourseProgressDto;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.service.AuthorizationService;
import net.therap.learningProcessor.service.CourseStudentService;
import net.therap.learningProcessor.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author avidewan
 * @since 8/31/25
 */
@WebMvcTest
@ContextConfiguration(classes = {CourseStudentController.class})
@AutoConfigureMockMvc
class CourseStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseStudentService courseStudentService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private HazelcastCacheService hazelcastCacheService;

    @MockitoBean
    private AuthorizationService authorizationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private StudentDto student;
    private StudentDto student2;
    private List<StudentDto> studentDtoList;

    private CourseDetailWithProgressDto courseDetail;
    private StudentCourseProgressDto courseProgress;

    @BeforeEach
    void setUp() {
        student = new StudentDto();
        student.setId(1L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@example.com");
        student.setGender("MALE");
        student.setDateOfBirth(LocalDate.of(2000, 1, 1));
        student.setPhone("01712345678");

        student2 = new StudentDto();
        student2.setId(2L);
        student2.setFirstName("student");
        student2.setLastName("two");
        student2.setEmail("student.doe@example.com");
        student2.setGender("MALE");
        student2.setDateOfBirth(LocalDate.of(2000, 1, 1));
        student2.setPhone("01712315678");

        studentDtoList = new ArrayList<>();
        studentDtoList.add(student);
        studentDtoList.add(student2);

        courseDetail = new CourseDetailWithProgressDto();
        courseDetail.setId(10L);
        courseDetail.setName("Test Course");
        courseDetail.setDescription("Course description");
        courseDetail.setInstructorId(100L);
        courseDetail.setInstructorName("Instructor Name");
        courseDetail.setProgress(0.0);
        courseDetail.setModules(Collections.emptyList());

        courseProgress = new StudentCourseProgressDto(
                1L, "John", "Doe", "john.doe@example.com", 10L, "Test Course", 25.0
        );
    }

    /** ---------------- POST /enrollments (void) ---------------- */
    @Test
    void testEnrollInCourse_Success() throws Exception {
        doNothing().when(authorizationService)
                .authorize(eq(AccessLevel.STUDENT_WITH_ID),
                        anyMap(),
                        any());

        doNothing().when(courseStudentService).enrollInCourse(1L, 10L);

        doNothing().when(notificationService).sendNotification(any());

        mockMvc.perform(post("/student-course/enrollments")
                        .param("studentId", "1")
                        .param("courseId", "10"))
                .andExpect(status().isCreated());

        verify(courseStudentService).enrollInCourse(1L, 10L);
        verify(notificationService).sendNotification(any());
    }

    /** ---------------- GET /enrollments/course/{courseId} with cache ---------------- */
    @Test
    void testGetStudentsEnrolledInCourse_CacheHit() throws Exception {

        doNothing().when(authorizationService).
                authorize(eq(AccessLevel.INSTRUCTOR_OF_COURSE),
                anyMap(),
                any());

        when(hazelcastCacheService.get(CacheConstants.STUDENTS_BY_COURSE, 10L))
                .thenReturn(studentDtoList);

        mockMvc.perform(get("/student-course/enrollments/course/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(courseStudentService, never()).getStudentsEnrolledInCourse(anyLong());
    }

    @Test
    void testGetStudentsEnrolledInCourse_CacheMiss() throws Exception {
        doNothing().when(authorizationService).
                authorize(eq(AccessLevel.INSTRUCTOR_OF_COURSE),
                        anyMap(),
                        any());

        when(hazelcastCacheService.get(CacheConstants.STUDENTS_BY_COURSE, 10L)).thenReturn(null);

        when(courseStudentService.getStudentsEnrolledInCourse(10L)).thenReturn(studentDtoList);

        mockMvc.perform(get("/student-course/enrollments/course/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(hazelcastCacheService).put(CacheConstants.STUDENTS_BY_COURSE, 10L, studentDtoList);
    }

    /** ---------------- PATCH /student-contents/.../complete ---------------- */
    @Test
    void testMarkContentCompleted_Success() throws Exception {
        doNothing().when(authorizationService).authorize(
                eq(AccessLevel.INSTRUCTOR_OF_COURSE_OR_STUDENT_ENROLLED_IN_COURSE),
                anyMap(),
                any()
        );

        when(courseStudentService.completeContent(1L, 100L)).thenReturn(true);

        mockMvc.perform(patch("/student-course/student-contents/student/1/course/10/content/100/complete"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testMarkContentCompleted_NotFound() throws Exception {
        doNothing().when(authorizationService).authorize(
                eq(AccessLevel.INSTRUCTOR_OF_COURSE_OR_STUDENT_ENROLLED_IN_COURSE),
                anyMap(),
                any());

        when(courseStudentService.completeContent(1L, 100L)).thenReturn(false);

        mockMvc.perform(patch("/student-course/student-contents/student/1/course/10/content/100/complete"))
                .andExpect(status().isNotFound());
    }

    /**  ---------------- POST /progress/{studentId} with cache ---------------- */
    @Test
    void testGetStudentCourseProgress_CacheHit() throws Exception {
        doNothing().when(authorizationService).authorize(eq(AccessLevel.STUDENT_WITH_ID),
                anyMap(),
                any());

        doNothing().when(authorizationService).authorize(eq(AccessLevel.STUDENT_ENROLLED_IN_COURSE),
                anyMap(),
                any());

        when(hazelcastCacheService.get(CacheConstants.STUDENT_COURSE_PROGRESS, "1:10"))
                .thenReturn(courseProgress);

        mockMvc.perform(post("/student-course/progress/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDetail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(1L))
                .andExpect(jsonPath("$.courseId").value(10L));

        verify(courseStudentService, never()).getStudentCourseProgress(anyLong(), any());
    }

    @Test
    void testGetStudentCourseProgress_CacheMiss() throws Exception {
        doNothing().when(authorizationService).authorize(eq(AccessLevel.STUDENT_WITH_ID),
                anyMap(),
                any());

        doNothing().when(authorizationService).authorize(eq(AccessLevel.STUDENT_ENROLLED_IN_COURSE),
                anyMap(),
                any());

        when(hazelcastCacheService.get(CacheConstants.STUDENT_COURSE_PROGRESS, "1:10"))
                .thenReturn(null);

        when(courseStudentService.getStudentCourseProgress(1L, courseDetail))
                .thenReturn(courseProgress);

        mockMvc.perform(post("/student-course/progress/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDetail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(1L));

        verify(hazelcastCacheService).put(CacheConstants.STUDENT_COURSE_PROGRESS, "1:10", courseProgress);
    }
}