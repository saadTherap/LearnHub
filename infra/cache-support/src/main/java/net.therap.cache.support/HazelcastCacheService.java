package net.therap.cache.support;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author riadanonto
 * @since 14/8/25
 */
public class HazelcastCacheService {

    private final HazelcastInstance hazelcastInstance;

    public HazelcastCacheService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public <K, V> void put(String mapName, K key, V value) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);
        map.put(key, value);
    }

    public <K, V> V get(String mapName, K key) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);
        return map.get(key);
    }

    public <K, V> Map<K, V> getAll(String mapName, Set<K> keys) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);
        return map.getAll(keys);
    }

    public <K, V> Collection<V> getAllValues(String mapName) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);
        return map.values();
    }

    public <K> void remove(String mapName, K key) {
        IMap<K, ?> map = hazelcastInstance.getMap(mapName);
        map.remove(key);
    }

    public void clear(String mapName) {
        IMap<?, ?> map = hazelcastInstance.getMap(mapName);
        map.clear();
    }

    public <K, V> boolean putIfAbsent(String mapName, K key, V value) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);
        return map.putIfAbsent(key, value) == null;
    }

    public <K, V> boolean replace(String mapName, K key, V oldValue, V newValue) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);
        return map.replace(key, oldValue, newValue);
    }

    public void removeKeysEndingWith(String mapName, String suffix) {
        if (hazelcastInstance == null) return;
        IMap<String, ?> map = hazelcastInstance.getMap(mapName);
        if (map == null) return;

        map.removeAll(Predicates.like("__key", "%" + suffix));
    }
}

