package net.therap.app.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class HazelcastCacheService {
    
    @Autowired(required = false)
    private HazelcastInstance hazelcastInstance;
    
    // Save or update an entity in cache
    public <K, V> void put(String mapName, K key, V value) {
        try {
            if (hazelcastInstance == null) return;
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            map.put(key, value);
        } catch (Exception e) {
            // Optionally log: Cache put failed, fallback to DB
        }
    }
    
    // Retrieve an entity by key
    public <K, V> V get(String mapName, K key) {
        try {
            if (hazelcastInstance == null) return null;
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.get(key);
        } catch (Exception e) {
            // Optionally log: Cache get failed, fallback to DB
            return null;
        }
    }
    
    // Retrieve multiple entities by keys
    public <K, V> Map<K, V> getAll(String mapName, Set<K> keys) {
        try {
            if (hazelcastInstance == null) return Collections.emptyMap();
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.getAll(keys);
        } catch (Exception e) {
            // Optionally log: Cache getAll failed, fallback to DB
            return Collections.emptyMap();
        }
    }
    
    // Retrieve all values from the map (be careful if map is large!)
    public <K, V> Collection<V> getAllValues(String mapName) {
        try {
            if (hazelcastInstance == null) return Collections.emptyList();
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.values();
        } catch (Exception e) {
            // Optionally log: Cache getAllValues failed
            return Collections.emptyList();
        }
    }
    
    // Remove entity by key
    public <K> void remove(String mapName, K key) {
        try {
            if (hazelcastInstance == null) return;
            IMap<K, ?> map = hazelcastInstance.getMap(mapName);
            map.remove(key);
        } catch (Exception e) {
            // Optionally log: Cache remove failed
        }
    }
    
    // Clear the whole map (use with caution!)
    public void clear(String mapName) {
        try {
            if (hazelcastInstance == null) return;
            IMap<?, ?> map = hazelcastInstance.getMap(mapName);
            map.clear();
        } catch (Exception e) {
            // Optionally log: Cache clear failed
        }
    }
    
    public <K, V> boolean putIfAbsent(String mapName, K key, V value) {
        try {
            if (hazelcastInstance == null) return false;
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.putIfAbsent(key, value) == null;
        } catch (Exception e) {
            // Optionally log: Cache putIfAbsent failed
            return false;
        }
    }
    
    public <K, V> boolean replace(String mapName, K key, V oldValue, V newValue) {
        try {
            if (hazelcastInstance == null) return false;
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.replace(key, oldValue, newValue);
        } catch (Exception e) {
            // Optionally log: Cache replace failed
            return false;
        }
    }
}