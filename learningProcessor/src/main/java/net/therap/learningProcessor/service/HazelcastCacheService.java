package net.therap.learningProcessor.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
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

        }
    }
    
    // Retrieve an entity by key
    public <K, V> V get(String mapName, K key) {
        try {
            if (hazelcastInstance == null) return null;
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.get(key);
        } catch (Exception e) {
            return null;
        }
    }
    
    // Retrieve multiple entities by keys
    public <K, V> Map<K, V> getAll(String mapName, Set<K> keys) {
        try {

            if (hazelcastInstance == null) return null;
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.getAll(keys);
        } catch (Exception e) {
           
            return null;

        }
    }
    
    // Retrieve all values from the map (be careful if map is large!)
    public <K, V> Collection<V> getAllValues(String mapName) {
        try {

            if (hazelcastInstance == null) return null;
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.values();
        } catch (Exception e) {
            // Optionally log
            return null;
        }
    }
    
    // Remove entity by key
    public <K> void remove(String mapName, K key) {
        try {
            if (hazelcastInstance == null) return;
            IMap<K, ?> map = hazelcastInstance.getMap(mapName);
            map.remove(key);
        } catch (Exception e) {

        }
    }
    
    // Clear the whole map (use with caution!)
    public void clear(String mapName) {
        try {
            if (hazelcastInstance == null) return;
            IMap<?, ?> map = hazelcastInstance.getMap(mapName);
            map.clear();
        } catch (Exception e) {

        }
    }
    
    public <K, V> boolean putIfAbsent(String mapName, K key, V value) {
        try {
            if (hazelcastInstance == null) return false;
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.putIfAbsent(key, value) == null;
        } catch (Exception e) {

            return false;
        }
    }
    
    public <K, V> boolean replace(String mapName, K key, V oldValue, V newValue) {
        try {
            if (hazelcastInstance == null) return false;
            IMap<K, V> map = hazelcastInstance.getMap(mapName);
            return map.replace(key, oldValue, newValue);
        } catch (Exception e) {

            return false;
        }
    }
}