package net.therap.app.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author riadanonto
 * @since 28/7/25
 */
@Component
public class HazelcastCacheService {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    // Save or update an entity in cache
    public <K, V> void put(String mapName, K key, V value) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);
        map.put(key, value);
    }

    // Retrieve an entity by key
    public <K, V> V get(String mapName, K key) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);

        return map.get(key);
    }

    // Retrieve multiple entities by keys
    public <K, V> Map<K, V> getAll(String mapName, Set<K> keys) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);

        return map.getAll(keys);
    }

    // Retrieve all values from the map (be careful if map is large!)
    public <K, V> Collection<V> getAllValues(String mapName) {
        IMap<K, V> map = hazelcastInstance.getMap(mapName);

        return map.values();
    }

    // Remove entity by key
    public <K> void remove(String mapName, K key) {
        IMap<K, ?> map = hazelcastInstance.getMap(mapName);
        map.remove(key);
    }

    // Clear the whole map (use with caution!)
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

}

