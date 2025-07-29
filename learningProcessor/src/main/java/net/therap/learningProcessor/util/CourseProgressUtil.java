package net.therap.learningProcessor.util;

import net.therap.learningProcessor.dto.CourseDetailDto;
import net.therap.learningProcessor.dto.ModuleDto;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.eum.CompletionStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author avidewan
 * @since 7/28/25
 */
public class CourseProgressUtil {

    public static double calculateCourseProgress(Map<Long, CompletionStatus> statusMap, List<ModuleDto> modules) {

        if (Objects.isNull(modules) || modules.isEmpty()) {
            return 0.0;
        }

        int total = 0, completed = 0;

        for (ModuleDto module : modules) {
            List<BaseContentDto> contents = module.getContents();

            if (Objects.isNull(contents) || contents.isEmpty()) {
                continue;
            }

            total += contents.size();
            completed += (int) contents.stream()
                    .filter(content -> statusMap.getOrDefault(content.getId(), CompletionStatus.NOT_COMPLETED) == CompletionStatus.COMPLETED)
                    .count();
        }

        double progress = (total == 0 ? 0.0 : (completed * 100.0) / total);

        return progress;
    }

    public static void enrichCourseDetailWithProgress(CourseDetailDto courseDetail, Map<Long, CompletionStatus> statusMap) {

        int total = 0, completed = 0;

        for (ModuleDto module : courseDetail.getModules()) {
            List<BaseContentDto> contents = module.getContents();

            if (Objects.isNull(contents) || contents.isEmpty()) {
                module.setCompletedContentCount(0);

                continue;
            }

            int moduleCompleted = 0;

            for (BaseContentDto content : contents) {
                CompletionStatus status = statusMap.getOrDefault(content.getId(), CompletionStatus.NOT_COMPLETED);
                content.setStatus(status);

                if (status == CompletionStatus.COMPLETED) {
                    moduleCompleted++;
                }
            }

            module.setCompletedContentCount(moduleCompleted);
            total += contents.size();
            completed += moduleCompleted;
        }

        courseDetail.setProgress(total == 0 ? 0.0 : (completed * 100.0) / total);
    }
}