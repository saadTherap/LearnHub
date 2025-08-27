//package net.therap.app.controller;
//
//import net.therap.app.dto.CourseDTO;
//import net.therap.app.model.Course;
//import net.therap.app.repository.CourseRepository;
//import net.therap.app.validation.OnCreate;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
///**
// * @author gazizafor
// * @since 27/8/25
// */
//
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//class CourseControllerFunctionalTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private CourseRepository courseRepository;
//
//    private String getBaseUrl() {
//        return "http://localhost:" + port + "/courses";
//    }
//
//    @BeforeEach
//    void setup() {
//        // Clear the database before each test
//        courseRepository.deleteAll();
//    }
//
//    @Test
//    void createCourse_withValidData_shouldReturnCreated() {
//        // Arrange
//        CourseDTO courseDTO = new CourseDTO();
//        courseDTO.setName("Test Course");
//        // Set other necessary fields for a valid DTO
//
//        // Act
//        ResponseEntity<CourseDTO> response = restTemplate.postForEntity(getBaseUrl() + "/draft", courseDTO, CourseDTO.class);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody().getId());
//    }
//
//    @Test
//    void createCourse_withInvalidData_shouldReturnBadRequest() {
//        // Arrange
//        CourseDTO courseDTO = new CourseDTO();
//        // Missing required fields to trigger validation error
//
//        // Act
//        ResponseEntity<CourseDTO> response = restTemplate.postForEntity(getBaseUrl() + "/draft", courseDTO, CourseDTO.class);
//
//        // Assert
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    void deleteCourse_existingCourse_shouldReturnNoContent() {
//        // Arrange
//        Course course = new Course();
//        // Set necessary fields
//        Course savedCourse = courseRepository.save(course);
//        long courseId = savedCourse.getId();
//
//        // Act
//        restTemplate.delete(getBaseUrl() + "/{id}", courseId);
//        Optional<Course> deletedCourse = courseRepository.findById(courseId);
//
//        // Assert
//        assertEquals(Optional.empty(), deletedCourse);
//    }
//}