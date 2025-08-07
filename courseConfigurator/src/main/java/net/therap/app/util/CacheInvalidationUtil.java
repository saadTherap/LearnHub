package net.therap.app.util;

import net.therap.app.service.HazelcastCacheService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
/**
 * @author riadanonto
 * @since 4/8/25
 */
@Component
public class CacheInvalidationUtil {

    private final HazelcastCacheService hazelcastCacheService;

    public CacheInvalidationUtil(HazelcastCacheService hazelcastCacheService) {
        this.hazelcastCacheService = hazelcastCacheService;
    }

    /**
     * Registers cache invalidation for given map names after current transaction commits.
     *
     * @param id                   Cache key to invalidate
     * @param mapNames             One or more cache map names
     */
    public void invalidateCacheAfterCommit(String id, String... mapNames) {
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

