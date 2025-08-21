package net.therap.learningProcessor.util;

import net.therap.learningProcessor.dto.CourseDetailWithProgressDto;
import net.therap.learningProcessor.dto.ModuleWithProgressDto;
import net.therap.learningProcessor.dto.StudentContentCompletionDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author avidewan
 * @since 7/28/25
 */
public class CourseProgressUtil {

    public static double calculateCourseProgress(List<StudentContentCompletionDto> completedContentDtos,
                                                 List<ModuleWithProgressDto> modules) {

        if (Objects.isNull(modules) || modules.isEmpty()) {
            return 0.0;
        }

        Set<Long> completedContentIds = completedContentDtos.stream()
                .map(StudentContentCompletionDto::getContentId)
                .collect(Collectors.toSet());

        int total = 0, completed = 0;

        for (ModuleWithProgressDto module : modules) {
            List<BaseContentDto> contents = module.getContents();

            if (Objects.isNull(contents) || contents.isEmpty()) {
                continue;
            }

            total += contents.size();
            completed += (int) contents.stream()
                    .filter(content -> completedContentIds.contains(content.getId()))
                    .count();
        }

        double progress = (total == 0 ? 0.0 : (completed * 100.0) / total);

        return progress;
    }

    public static void addProgressDetailsToCourse(CourseDetailWithProgressDto courseDetail, List<StudentContentCompletionDto> completedContentDtos) {

        if (Objects.isNull(courseDetail.getModules()) || courseDetail.getModules().isEmpty()) {
            courseDetail.setProgress(0);

            return;
        }

        Set<Long> completedContentIds = completedContentDtos.stream()
                .map(StudentContentCompletionDto::getContentId)
                .collect(Collectors.toSet());

        int total = 0, completed = 0;

        for (ModuleWithProgressDto module : courseDetail.getModules()) {
            List<BaseContentDto> contents = module.getContents();

            if (Objects.isNull(contents) || contents.isEmpty()) {
                module.setCompletedContentCount(0);

                continue;
            }

            int moduleCompleted = 0;

            for (BaseContentDto content : contents) {
                boolean isCompleted = completedContentIds.contains(content.getId());

                if (isCompleted) {
                    content.setCompleted(true);
                    moduleCompleted++;
                }
            }

            module.setCompletedContentCount(moduleCompleted);
            module.setNumberOfContents(contents.size());

            total += contents.size();
            completed += moduleCompleted;
        }

        courseDetail.setProgress(total == 0 ? 0.0 : (completed * 100.0) / total);
    }
}