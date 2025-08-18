package net.therap.learningProcessor.service;

import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.cache.support.HazelcastCacheService;
import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.entity.UpdateInfo;
import net.therap.cache.support.CacheInvalidationUtil;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateInfoService {
  
    private final CacheInvalidationUtil cacheInvalidationUtil;
    private final HazelcastCacheService hazelcastCacheService;

    public void invalidateCache(UpdateInfo updateInfo) {
        if (updateInfo == null) {
            log.debug("UpdateInfo is null, skip cache invalidation.");
            return;
        }
        final Long courseId  = updateInfo.getCourseId();
        final Long moduleId  = updateInfo.getModuleId();
        final Long contentId = updateInfo.getContentId();

        cacheInvalidationUtil.invalidateCachesAfterCommit(
                courseId,
                CacheConstants.ALL_STUDENT_PROGRESS_BY_COURSE,
                CacheConstants.STUDENTS_BY_COURSE,
                CacheConstants.COURSE_CATALOG_LP
        );

        cacheInvalidationUtil.invalidateCachesAfterCommit(moduleId, CacheConstants.MODULE_CONTENTS);
        cacheInvalidationUtil.invalidateCachesAfterCommit(contentId, CacheConstants.CONTENT_DETAIL);

        String suffix = ":" + courseId;
        hazelcastCacheService.removeKeysEndingWith(CacheConstants.COURSE_PROGRESS_DETAIL, suffix);
        hazelcastCacheService.removeKeysEndingWith(CacheConstants.STUDENT_COURSE_PROGRESS, suffix);

        log.info("Invalidated caches for UpdateInfo(courseId={}, moduleId={}, contentId={})",
                courseId, moduleId, contentId);
    }
}

