package net.therap.app.service;

import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import net.therap.app.repository.CourseRepository;
import net.therap.app.repository.InstructorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author gazizafor
 * @since 31/8/25
 */
@SpringBootTest
//@EnableAutoConfiguration
//@ContextConfiguration(classes = {CourseService.class, CourseRepository.class, InstructorRepository.class})
@Transactional
@ActiveProfiles("test")
class CourseServiceIntegrationTest {
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private InstructorService instructorService;
    
    @Test
    void testSaveCourse_shouldPersistToDatabase() {
        // Arrange
        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");
        instructor.setEmail("test@example.com");
        instructor.setDateOfBirth(LocalDate.of(1990, 1, 1));
        // Save the instructor first to satisfy the foreign key constraint
        instructor = instructorService.createInstructor(instructor);
        
        Course course = new Course();
        course.setName("Spring Boot Testing");
        course.setInstructor(instructor);
//        course.setDeleted(false);
        
        // Act
        Course savedCourse = courseService.save(course);
        
        // Assert
        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getId()).isGreaterThan(0);
        
        // Verify that the course was actually saved to the database by finding it again
        Optional<Course> foundCourse = courseService.findById(savedCourse.getId());
        
        assertThat(foundCourse).isPresent();
        assertThat(foundCourse.get().getName()).isEqualTo("Spring Boot Testing");
    }
    
    @Test
    void testFindById_shouldReturnCorrectCourse() {
        // Arrange
        Instructor instructor = new Instructor();
        instructor.setName("Test Instructor");
        instructor.setEmail("test@example.com");
        instructor.setDateOfBirth(LocalDate.of(1990, 1, 1));
        instructor = instructorService.createInstructor(instructor);
        
        Course course = new Course();
        course.setName("Another Test Course");
        course.setInstructor(instructor);
        Course savedCourse = courseService.save(course);
        
        // Act
        Optional<Course> foundCourse = courseService.findById(savedCourse.getId());
        
        // Assert
        assertThat(foundCourse).isPresent();
        assertThat(foundCourse.get().getName()).isEqualTo("Another Test Course");
    }
}