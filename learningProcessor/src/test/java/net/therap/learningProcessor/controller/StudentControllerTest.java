package net.therap.learningProcessor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.therap.cache.support.HazelcastCacheService;
import net.therap.learningProcessor.advice.GlobalExceptionHandler;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.service.AuthorizationService;
import net.therap.learningProcessor.service.StudentService;
import net.therap.learningProcessor.util.StudentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author avidewan
 * @since 8/28/25
 */
@WebMvcTest
@ContextConfiguration(classes = {StudentController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private HazelcastCacheService hazelcastCacheService;

    @MockitoBean
    private AuthorizationService authorizationService;

    @MockitoBean
    private StudentUtil studentUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private StudentDto student;
    private List<StudentDto> studentList;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        student = new StudentDto();
        student.setId(1L);
        student.setEmail("test@student.com");
        student.setFirstName("Test");
        student.setLastName("Student");

        StudentDto student2 = new StudentDto();
        student2.setId(2L);
        student2.setEmail("test2@student.com");
        student2.setFirstName("Test2");
        student2.setLastName("Student2");

        studentList = Arrays.asList(student, student2);
    }

    /** --------------------- GET /students --------------------- */
    @Test
    void testGetAllStudents_Success() throws Exception {

        when(studentService.getAllStudents()).thenReturn(studentList);

        doNothing().when(authorizationService).authorize(eq(AccessLevel.INSTRUCTOR_ONLY), any());

        mockMvc.perform(get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(student.getId()))
                .andExpect(jsonPath("$[0].email").value(student.getEmail()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].email").value("test2@student.com"));

        verify(authorizationService).authorize(eq(AccessLevel.INSTRUCTOR_ONLY), any());
        verify(studentService).getAllStudents();
    }

    /** --------------------- GET /students/{id} FROM CACHE --------------------- */
    @Test
    void testGetStudentById_FromCache_Success() throws Exception {
        when(hazelcastCacheService.get(CacheConstants.STUDENTS, 1L)).thenReturn(student);
        doNothing().when(authorizationService).authorize(eq(AccessLevel.INSTRUCTOR_OR_STUDENT_WITH_ID), anyMap(), any());

        mockMvc.perform(get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.email").value(student.getEmail()))
                .andExpect(jsonPath("$.firstName").value(student.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(student.getLastName()));

        verify(authorizationService).authorize(eq(AccessLevel.INSTRUCTOR_OR_STUDENT_WITH_ID),
                eq(Map.of("studentId", 1L)),
                any());
        verify(hazelcastCacheService).get(CacheConstants.STUDENTS, 1L);
        verify(studentService, never()).getStudentById(1L);
        verify(hazelcastCacheService, never()).put(any(), any(), any());
    }

    @Test
    void testGetStudentById_FromDatabase_Success() throws Exception {
        when(hazelcastCacheService.get(CacheConstants.STUDENTS, 1L)).thenReturn(null);
        when(studentService.getStudentById(1L)).thenReturn(student);
        doNothing().when(authorizationService).authorize(eq(AccessLevel.INSTRUCTOR_OR_STUDENT_WITH_ID), anyMap(), any());

        mockMvc.perform(get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.email").value(student.getEmail()));

        verify(authorizationService).authorize(eq(AccessLevel.INSTRUCTOR_OR_STUDENT_WITH_ID),
                eq(Map.of("studentId", 1L)),
                any());
        verify(hazelcastCacheService).get(CacheConstants.STUDENTS, 1L);
        verify(studentService).getStudentById(1L);
        verify(hazelcastCacheService).put(CacheConstants.STUDENTS, 1L, student);
    }

    @Test
    void testGetStudentById_NotFound() throws Exception {
        when(hazelcastCacheService.get(CacheConstants.STUDENTS, 1L)).thenReturn(null);
        when(studentService.getStudentById(1L)).thenReturn(null);
        doNothing().when(authorizationService).authorize(eq(AccessLevel.INSTRUCTOR_OR_STUDENT_WITH_ID),
                anyMap(),
                any());

        mockMvc.perform(get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(authorizationService).authorize(eq(AccessLevel.INSTRUCTOR_OR_STUDENT_WITH_ID),
                eq(Map.of("studentId", 1L)),
                any());
        verify(hazelcastCacheService).get(CacheConstants.STUDENTS, 1L);
        verify(studentService).getStudentById(1L);
        verify(hazelcastCacheService, never()).put(any(), any(), any());
    }

    /** --------------------- POST /students (CREATE) --------------------- */
    @Test
    void testCreateStudent_ValidationSuccess() throws Exception {
        StudentDto inputStudent = new StudentDto();
        inputStudent.setFirstName("John");
        inputStudent.setLastName("Doe");
        inputStudent.setGender("MALE");
        inputStudent.setDateOfBirth(LocalDate.of(2000, 1, 1));
        inputStudent.setEmail("john.doe@example.com");
        inputStudent.setPhone("01712345678");

        StudentDto createdStudent = new StudentDto();
        createdStudent.setId(10L);
        createdStudent.setFirstName("John");
        createdStudent.setLastName("Doe");
        createdStudent.setGender("MALE");
        createdStudent.setEmail("john.doe@example.com");
        createdStudent.setPhone("01712345678");

        when(studentService.createStudent(ArgumentMatchers.any(StudentDto.class))).thenReturn(createdStudent);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputStudent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }


    @Test
    void testCreateStudent_ValidationFailure() throws Exception {
        StudentDto inputStudent = new StudentDto();

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputStudent)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details.firstName").exists())
                .andExpect(jsonPath("$.details.lastName").exists())
                .andExpect(jsonPath("$.details.gender").exists())
                .andExpect(jsonPath("$.details.email").exists());
    }

    /** --------------------- PUT /students (UPDATE) --------------------- */

    @Test
    void testUpdateStudent_ValidationSuccess() throws Exception {
        StudentDto inputStudent = new StudentDto();
        inputStudent.setFirstName("Updated");
        inputStudent.setLastName("Name");
        inputStudent.setGender("FEMALE");
        inputStudent.setEmail("updated@example.com");
        inputStudent.setPhone("01798765432");

        StudentDto updatedStudent = new StudentDto();
        updatedStudent.setId(1L);
        updatedStudent.setFirstName("Updated");
        updatedStudent.setLastName("Name");
        updatedStudent.setGender("FEMALE");
        updatedStudent.setEmail("updated@example.com");
        updatedStudent.setPhone("01798765432");

        when(studentService.updateStudent(ArgumentMatchers.any(StudentDto.class))).thenReturn(updatedStudent);
        doNothing().when(authorizationService).authorize(eq(AccessLevel.STUDENT_WITH_ID), anyMap(), any());

        mockMvc.perform(put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testUpdateStudent_NotFound() throws Exception {
        when(studentService.updateStudent(ArgumentMatchers.any(StudentDto.class))).thenReturn(null);
        doNothing().when(authorizationService).authorize(eq(AccessLevel.STUDENT_WITH_ID),
                anyMap(),
                any());

        mockMvc.perform(put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isNotFound());

        verify(authorizationService).authorize(eq(AccessLevel.STUDENT_WITH_ID), eq(Map.of("studentId", 1L)), any());
        verify(studentService).updateStudent(ArgumentMatchers.any(StudentDto.class));
    }

    /** --------------------- DELETE /students/{id} --------------------- */
    @Test
    void testDeleteStudent_Success() throws Exception {
        when(studentService.deleteStudent(1L)).thenReturn(true);
        doNothing().when(authorizationService).authorize(eq(AccessLevel.STUDENT_WITH_ID),
                anyMap(),
                any());

        mockMvc.perform(delete("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(authorizationService).authorize(eq(AccessLevel.STUDENT_WITH_ID), eq(Map.of("studentId", 1L)), any());
        verify(studentService).deleteStudent(1L);
    }

    @Test
    void testDeleteStudent_NotFound() throws Exception {
        when(studentService.deleteStudent(1L)).thenReturn(false);
        doNothing().when(authorizationService).authorize(eq(AccessLevel.STUDENT_WITH_ID), anyMap(), any());

        mockMvc.perform(delete("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(authorizationService).authorize(eq(AccessLevel.STUDENT_WITH_ID), eq(Map.of("studentId", 1L)), any());
        verify(studentService).deleteStudent(1L);
    }

    /** --------------------- GET /students/email/{email} --------------------- */
    @Test
    void testGetStudentByEmail_Success() throws Exception {
        when(studentService.getStudentByEmail("test@student.com")).thenReturn(student);

        mockMvc.perform(get("/students/email/test@student.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.email").value(student.getEmail()))
                .andExpect(jsonPath("$.firstName").value(student.getFirstName()));

        verify(studentService).getStudentByEmail("test@student.com");
    }

    @Test
    void testGetStudentByEmail_NotFound() throws Exception {
        when(studentService.getStudentByEmail("nonexistent@student.com")).thenReturn(null);

        mockMvc.perform(get("/students/email/nonexistent@student.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(studentService).getStudentByEmail("nonexistent@student.com");
    }

    /** --------------------- GET /students/fromToken --------------------- */
    @Test
    void testGetStudentFromToken_Success() throws Exception {
        when(studentUtil.getStudentFromRequest(any())).thenReturn(student);

        mockMvc.perform(get("/students/fromToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.email").value(student.getEmail()));

        verify(studentUtil).getStudentFromRequest(any());
    }

    @Test
    void testGetStudentFromToken_NotFound() throws Exception {
        when(studentUtil.getStudentFromRequest(any())).thenReturn(null);

        mockMvc.perform(get("/students/fromToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(studentUtil).getStudentFromRequest(any());
    }
}