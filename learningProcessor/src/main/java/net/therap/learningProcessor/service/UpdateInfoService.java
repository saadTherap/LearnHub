package net.therap.learningProcessor.service;

import net.therap.learningProcessor.constants.CacheConstants;
import net.therap.learningProcessor.entity.UpdateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@Service
public class UpdateInfoService {

    @Autowired
    private final HazelcastCacheService hazelcastCacheService;

    public UpdateInfoService(HazelcastCacheService hazelcastCacheService) {
        this.hazelcastCacheService = hazelcastCacheService;
    }

    public void invalidateCache(UpdateInfo updateInfo) {
        System.out.println("invalidateCache from hazlecast - KafkaUpdateInfoService");

        hazelcastCacheService.remove(CacheConstants.COURSE_CATALOG, updateInfo.getCourseId());
        hazelcastCacheService.remove(CacheConstants.COURSES, updateInfo.getCourseId());
    }
}
