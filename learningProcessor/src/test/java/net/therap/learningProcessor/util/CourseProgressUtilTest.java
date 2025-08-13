package net.therap.learningProcessor.util;

import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.ModuleWithProgressDto;
import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author avidewan
 * @since 8/4/25
 */
public class CourseProgressUtilTest {

    @Test
    void calculateCourseProgress_shouldReturnCorrectPercentage() {

        var content1 = content(101L);
        var content2 = content(102L);
        var content3 = content(103L);

        var module = new ModuleWithProgressDto();
        module.setContents(List.of(content1, content2, content3));

        var completed = List.of(
                completion(1L,101L),
                completion(1L,102L)
        );

        double progress = CourseProgressUtil.calculateCourseProgress(completed, List.of(module));

        assertThat(progress).isEqualTo(66.66666666666667);
    }

    @Test
    void calculateCourseProgress_emptyModules_shouldReturnZero() {
        double progress = CourseProgressUtil.calculateCourseProgress(List.of(), List.of());

        assertThat(progress).isEqualTo(0.0);
    }

    @Test
    void addProgressDetailsToCourse_shouldSetProgressAndCounts() {
        var content1 = content(201L);
        var content2 = content(202L);

        var module1 = new ModuleWithProgressDto();
        module1.setContents(List.of(content1, content2));

        var course = new CourseDetailWithProgressDto();
        course.setModules(List.of(module1));

        var completed = List.of(completion(1L,201L));

        CourseProgressUtil.addProgressDetailsToCourse(course, completed);

        assertThat(course.getProgress()).isEqualTo(50.0);
        assertThat(course.getModules().get(0).getCompletedContentCount()).isEqualTo(1);
        assertThat(course.getModules().get(0).getNumberOfContents()).isEqualTo(2);
    }

    @Test
    void addProgressDetailsToCourse_withEmptyContents_shouldSetZeroCounts() {
        var module = new ModuleWithProgressDto();
        var course = new CourseDetailWithProgressDto();
        course.setModules(List.of(module));

        CourseProgressUtil.addProgressDetailsToCourse(course, List.of());

        assertThat(course.getProgress()).isEqualTo(0.0);
        assertThat(course.getModules().get(0).getCompletedContentCount()).isEqualTo(0);
        assertThat(course.getModules().get(0).getNumberOfContents()).isEqualTo(0);
    }

    private BaseContentDto content(long id) {
        return new BaseContentDto() {{
            setId(id);
        }};
    }

    private StudentContentCompletionDto completion(long studentId, long contentId) {
        return new StudentContentCompletionDto(studentId, contentId);
    }
}