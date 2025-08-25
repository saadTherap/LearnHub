package net.therap.learningProcessor.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.cache.support.HazelcastCacheService;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.dto.StudentDto;
import net.therap.learningProcessor.eum.AccessLevel;
import net.therap.learningProcessor.service.AuthorizationService;
import net.therap.learningProcessor.service.StudentService;
import net.therap.learningProcessor.util.StudentUtil;
import net.therap.learningProcessor.validator.group.OnCreate;
import net.therap.learningProcessor.validator.group.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author avidewan
 * @since 7/27/25
 */
@Slf4j
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Validated
public class StudentController {

    private final StudentService studentService;
    private final HazelcastCacheService hazelcastCacheService;
    private final AuthorizationService authorizationService;
    private final StudentUtil studentUtil;

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents(HttpServletRequest request) {
        log.info("[StudentController] Request to fetch all students");

        authorizationService.authorize(AccessLevel.INSTRUCTOR_ONLY, request);
        log.debug("[StudentController] Authorization successful for INSTRUCTOR_ONLY");

        List<StudentDto> students = studentService.getAllStudents();
        log.info("[StudentController] Fetched {} students", students.size());

        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id, HttpServletRequest request) {
        log.info("[StudentController] Fetch student by ID: {}", id);

        authorizationService.authorize(AccessLevel.INSTRUCTOR_OR_STUDENT_WITH_ID,  Map.of("studentId", id), request);
        log.debug("[StudentController] Authorization successful for ID {}", id);

        StudentDto cachedStudent = hazelcastCacheService.get(CacheConstants.STUDENTS, id);

        if (Objects.nonNull(cachedStudent)) {
            log.info("[StudentController] Student ID {} found in cache", id);

            return ResponseEntity.ok(cachedStudent);
        }

        StudentDto student = studentService.getStudentById(id);

        if (Objects.isNull(student)) {
            log.warn("[StudentController] Student ID {} not found", id);

            return ResponseEntity.notFound().build();
        }

        hazelcastCacheService.put(CacheConstants.STUDENTS, id, student);
        log.info("[StudentController] Student ID {} fetched from DB", id);

        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@Validated(OnCreate.class) @RequestBody StudentDto studentDto) {
        log.info("[StudentController] Create student request: {}", studentDto.getEmail());

        StudentDto created = studentService.createStudent(studentDto);
        log.info("[StudentController] Student created with ID {}", created.getId());

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id,
                                                    @Validated(OnUpdate.class) @RequestBody StudentDto studentDto,
                                                    HttpServletRequest request) {

        log.info("[StudentController] Update request for student ID {}", id);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", id), request);
        log.debug("[StudentController] Authorization successful for update of ID {}", id);

        studentDto.setId(id);
        StudentDto updatedStudent = studentService.updateStudent(studentDto);

        if (Objects.isNull(updatedStudent)) {
            log.warn("[StudentController] Update failed, student ID {} not found", id);

            return ResponseEntity.notFound().build();
        }

        log.info("[StudentController] Student ID {} updated successfully", id);

        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id, HttpServletRequest request) {
        log.info("[StudentController] Delete request for student ID {}", id);

        authorizationService.authorize(AccessLevel.STUDENT_WITH_ID, Map.of("studentId", id), request);
        log.debug("[StudentController] Authorization successful for delete of ID {}", id);

        boolean deleted = studentService.deleteStudent(id);

        if (!deleted) {
            log.warn("[StudentController] Delete failed, student ID {} not found", id);

            return ResponseEntity.notFound().build();
        }

        log.info("[StudentController] Student ID {} deleted successfully", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<StudentDto> getStudentByEmail(@PathVariable String email, HttpServletRequest request) {
        log.info("[StudentController] Fetch student by email: {}", email);

        StudentDto student = studentService.getStudentByEmail(email);

        if (Objects.isNull(student)) {
            log.warn("[StudentController] Student with email {} not found", email);

            return ResponseEntity.notFound().build();
        }

        log.info("[StudentController] Student with email {} fetched successfully", email);

        return ResponseEntity.ok(student);
    }

    @GetMapping("/fromToken")
    public ResponseEntity<StudentDto> getStudentFromToken(HttpServletRequest request) {
        log.info("[StudentController] Fetch student from token");

        StudentDto student = studentUtil.getStudentFromRequest(request);

        if (Objects.isNull(student)) {
            log.warn("[StudentController] Student not found from token");

            return ResponseEntity.notFound().build();
        }

        log.info("[StudentController] Student fetched from token: {}", student.getId());

        return ResponseEntity.ok(student);
    }
}