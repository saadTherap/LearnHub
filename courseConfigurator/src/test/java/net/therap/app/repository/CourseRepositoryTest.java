package net.therap.app.repository;

import net.therap.app.model.Course;
import net.therap.app.model.Instructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableAutoConfiguration(exclude = {
        org.springframework.cloud.openfeign.FeignAutoConfiguration.class
})
@ActiveProfiles("test")
class CourseRepositoryTest {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Test
    void testSaveCourse() {
        Instructor instructor = new Instructor();
        instructor.setName("Jane Doe");
        instructor.setEmail("jane@example.com");
        
        instructor = instructorRepository.save(instructor);
        
        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("Sample description");
        course.setCurrentRelease(1L);
        course.setInstructor(instructor);
        
        Course saved = courseRepository.save(course);
        
        assertThat(saved.getId()).isGreaterThan(0);
        assertThat(saved.getInstructor().getName()).isEqualTo("Jane Doe");
    }
}
