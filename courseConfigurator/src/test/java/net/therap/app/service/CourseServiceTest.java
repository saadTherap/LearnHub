package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Course;
import net.therap.app.repository.CourseRepository;
import net.therap.cache.support.CacheInvalidationUtil;
import net.therap.cache.support.HazelcastCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author riadanonto
 * @since 13/8/25
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock private CacheInvalidationUtil cacheInvalidationUtil;
    @Mock private CourseRepository courseRepository;
    @Mock private ModuleService moduleService;
    @Mock private HazelcastCacheService hazelcastCacheService;
    @Mock private MessageSource messageSource;
    @Mock private DtoHelper dtoHelper;
    @Mock private ContentService contentService;

    @InjectMocks
    private CourseService courseService;

    @Test
    void testFindAll() {
        List<Course> courses = List.of(new Course(), new Course());
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.findAll();

        assertEquals(2, result.size());
        verify(courseRepository).findAll();
        verifyNoMoreInteractions(courseRepository);
    }

    @Test
    void testSaveCourse_callsCacheInvalidationUtil_afterSave() {
        // Arrange
        Course course = new Course(1L, "New Course", null, 0L, null, null, null);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        Course savedCourse = courseService.save(course);

        // Assert
        assertNotNull(savedCourse);
        assertEquals(1L, savedCourse.getId());
        verify(courseRepository).save(any(Course.class));

        // Verify util is called with expected arguments (string key if your util expects String)
        verify(cacheInvalidationUtil).invalidateCachesAfterCommit(
                eq(String.valueOf(1L)),
                eq(CacheConstants.COURSES),
                eq(CacheConstants.COURSE_CATALOG)
        );
        verifyNoMoreInteractions(cacheInvalidationUtil);
    }

    @Test
    void deleteById_softDeletes_andDelegatesInvalidation_toUtil() {
        // Arrange
        Course course = new Course();
        course.setId(1L);
        course.setDeleted(false);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        TransactionSynchronizationManager.initSynchronization();
        try {
            courseService.deleteById(1L);

            // fire afterCommit hooks
            for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
                try { sync.afterCommit(); } catch (Exception ignored) {}
            }
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }

        // Assert: entity was soft-deleted
        ArgumentCaptor<Course> savedCaptor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(savedCaptor.capture());
        assertThat(savedCaptor.getValue().isDeleted()).isTrue();

        // Assert: util got called to invalidate both caches for the course id
        verify(cacheInvalidationUtil).invalidateCachesAfterCommit(
                eq(String.valueOf(1L)),
                eq(CacheConstants.COURSES),
                eq(CacheConstants.COURSE_CATALOG)
        );

        verifyNoMoreInteractions(cacheInvalidationUtil);
        verify(courseRepository).findById(1L);
        verifyNoMoreInteractions(courseRepository);
    }
}

