package net.therap.learningProcessor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.therap.cache.support.HazelcastCacheService;
import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.ModuleWithProgressDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.entity.StudentContentCompletion;
import net.therap.learningProcessor.repository.CourseEnrollmentRepository;
import net.therap.learningProcessor.repository.StudentContentCompletionRepository;
import net.therap.learningProcessor.repository.StudentRepository;
import net.therap.learningProcessor.service.AuthorizationService;
import net.therap.learningProcessor.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CourseStudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentContentCompletionRepository studentContentCompletionRepository;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorizationService authorizationService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private HazelcastCacheService hazelcastCacheService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        Mockito.doNothing().when(authorizationService)
                .authorize(Mockito.any(), Mockito.anyMap(), Mockito.any());

        Mockito.doNothing().when(notificationService)
                .sendNotification(Mockito.any());

        Mockito.doNothing().when(hazelcastCacheService)
                        .put(Mockito.any(), Mockito.any(), Mockito.any());

        when(hazelcastCacheService.get(Mockito.any(), Mockito.any()))
                .thenReturn(null);

        studentContentCompletionRepository.deleteAll();
        courseEnrollmentRepository.deleteAll();
        studentRepository.deleteAll();

        testStudent = new Student();
        testStudent.setFirstName("Alice");
        testStudent.setLastName("Wanda");
        testStudent.setEmail("alice@example.com");
        testStudent = studentRepository.save(testStudent);
    }

    /** --------------------- HELPERS --------------------- */
    private CourseDetailWithProgressDto createCourseDto(long courseId, String name, List<Long> contentIds) {
        CourseDetailWithProgressDto course = new CourseDetailWithProgressDto();
        course.setId(courseId);
        course.setName(name);

        ModuleWithProgressDto module = new ModuleWithProgressDto();
        module.setId(courseId * 10);
        module.setTitle("Module 1");

        module.setContents(contentIds.stream().map(id -> {
            BaseContentDto content = new BaseContentDto();
            content.setId(id);
            return content;
        }).toList());

        course.setModules(List.of(module));
        return course;
    }

    private void enrollStudent(Long studentId, Long courseId) throws Exception {
        mockMvc.perform(post("/student-course/enrollments")
                        .param("studentId", String.valueOf(studentId))
                        .param("courseId", String.valueOf(courseId)))
                .andExpect(status().isCreated());
    }

    private void addContentCompletion(Long studentId, Long contentId) {
        var completion = new StudentContentCompletion();
        completion.setStudent(testStudent);
        completion.setContentId(contentId);
        studentContentCompletionRepository.save(completion);
    }

    /** --------------------- ENROLL --------------------- */
    @Test
    void testEnrollStudentInCourse() throws Exception {
        mockMvc.perform(post("/student-course/enrollments")
                        .param("studentId", String.valueOf(testStudent.getId()))
                        .param("courseId", "100"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/student-course/enrollments/course/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testStudent.getId()));
    }

    @Test
    void testEnrollStudentAuthorizationFailure() throws Exception {
        Mockito.doThrow(new RuntimeException("Unauthorized"))
                .when(authorizationService)
                .authorize(Mockito.any(), Mockito.anyMap(), Mockito.any());

        mockMvc.perform(post("/student-course/enrollments")
                        .param("studentId", String.valueOf(testStudent.getId()))
                        .param("courseId", "101"))
                .andExpect(status().is5xxServerError()); // or your app-specific exception handling
    }

    /** --------------------- ENROLLED COURSES --------------------- */
    @Test
    void testGetEnrolledCoursesByStudent() throws Exception {
        enrollStudent(testStudent.getId(), 200L);

        mockMvc.perform(get("/student-course/enrollments/student/" + testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(200));
    }

    /** --------------------- COMPLETE CONTENT --------------------- */
    @Test
    void testCompleteContent() throws Exception {
        enrollStudent(testStudent.getId(), 300L);

        mockMvc.perform(patch("/student-course/student-contents/student/" + testStudent.getId() +
                        "/course/300/content/900/complete"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/student-course/content-status/student/" + testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contentId").value(900));
    }

    /** --------------------- COURSE DETAIL / PROGRESS --------------------- */
    @Test
    void testCourseDetailWithProgress() throws Exception {
        var courseDto = createCourseDto(400L, "Java Basics", List.of(1000L, 1001L));

        addContentCompletion(testStudent.getId(), 1000L);
        enrollStudent(testStudent.getId(), 400L);

        String json = objectMapper.writeValueAsString(courseDto);

        mockMvc.perform(post("/student-course/progress/detailed/" + testStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(400))
                .andExpect(jsonPath("$.modules[0].completedContentCount").value(1))
                .andExpect(jsonPath("$.progress").value(50.0));
    }

    /** --------------------- STUDENT COURSE PROGRESS --------------------- */
    @Test
    void testGetStudentCourseProgress() throws Exception {
        var courseDto = createCourseDto(500L, "Spring Boot", List.of(2000L, 2001L));
        addContentCompletion(testStudent.getId(), 2000L);
        enrollStudent(testStudent.getId(), 500L);

        String json = objectMapper.writeValueAsString(courseDto);

        mockMvc.perform(post("/student-course/progress/" + testStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(testStudent.getId()))
                .andExpect(jsonPath("$.courseId").value(500))
                .andExpect(jsonPath("$.progress").value(50.0));
    }

    /** --------------------- ALL STUDENT PROGRESS FOR COURSE --------------------- */
    @Test
    void testGetAllStudentProgressForCourse() throws Exception {
        var courseDto = createCourseDto(600L, "Kotlin Basics", List.of(3000L, 3001L, 3002L));
        addContentCompletion(testStudent.getId(), 3000L);
        addContentCompletion(testStudent.getId(), 3002L);
        enrollStudent(testStudent.getId(), 600L);

        String json = objectMapper.writeValueAsString(courseDto);

        mockMvc.perform(post("/student-course/progress/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].studentId").value(testStudent.getId()))
                .andExpect(jsonPath("$[0].progress").value(66.66666666666667));
    }
}
