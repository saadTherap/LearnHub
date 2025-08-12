package net.therap.learningProcessor.util;

import org.springframework.stereotype.Component;
import net.therap.learningProcessor.service.HazelcastCacheService;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author riadanonto
 * @since 3/8/25
 */
@Component
public class CacheInvalidationUtil {

    private final HazelcastCacheService hazelcastCacheService;

    public CacheInvalidationUtil(HazelcastCacheService hazelcastCacheService) {
        this.hazelcastCacheService = hazelcastCacheService;
    }

    public void invalidateCachesAfterCommit(String id, String... mapNames) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (String mapName : mapNames) {
                    hazelcastCacheService.remove(mapName, id);
                }
            }
        });
    }
}

