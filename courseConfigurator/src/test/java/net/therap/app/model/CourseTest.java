package net.therap.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author riadanonto
 * @since 13/8/25
 */
public class CourseTest {

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
    }

    @Test
    void testSettersAndGetters() {
        // Given
        long id = 1L;
        String name = "Test Course";
        String description = "A description.";
        long currentRelease = 1L;
        String imageUrl = "http://example.com/image.png";
        Instructor instructor = new Instructor();
        instructor.setId(10L);

        // When
        course.setId(id);
        course.setName(name);
        course.setDescription(description);
        course.setCurrentRelease(currentRelease);
        course.setImageUrl(imageUrl);
        course.setInstructor(instructor);
        course.setModules(Collections.emptyList());

        // Then
        assertEquals(id, course.getId());
        assertEquals(name, course.getName());
        assertEquals(description, course.getDescription());
        assertEquals(currentRelease, course.getCurrentRelease());
        assertEquals(imageUrl, course.getImageUrl());
        assertEquals(instructor, course.getInstructor());
        assertTrue(course.getModules().isEmpty());
    }

}