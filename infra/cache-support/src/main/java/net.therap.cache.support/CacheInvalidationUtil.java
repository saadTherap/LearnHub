package net.therap.cache.support;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author riadanonto
 * @since 14/8/25
 */
public class CacheInvalidationUtil {

    private final HazelcastCacheService hazelcastCacheService;

    public CacheInvalidationUtil(HazelcastCacheService hazelcastCacheService) {
        this.hazelcastCacheService = hazelcastCacheService;
    }

    /**
     * Registers cache invalidation after commit. If no tx is active,
     * invalidates immediately (useful in async/listener contexts).
     */
    public <K> void invalidateCachesAfterCommit(K id, String... mapNames) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (String mapName : mapNames) {
                        hazelcastCacheService.remove(mapName, id);
                    }
                }
            });
        } else {
            // no tx -> do now
            for (String mapName : mapNames) {
                hazelcastCacheService.remove(mapName, id);
            }
        }
    }
}

