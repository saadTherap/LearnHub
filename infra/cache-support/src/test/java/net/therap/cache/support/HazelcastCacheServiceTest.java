package net.therap.cache.support;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author riadanonto
 * @since 13/8/25
 */
@ExtendWith(MockitoExtension.class)
public class HazelcastCacheServiceTest {

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private IMap iMap;

    @InjectMocks
    private HazelcastCacheService hazelcastCacheService;

    private static final String MAP_NAME = "testMap";
    private static final String TEST_KEY = "testKey";
    private static final String TEST_VALUE = "testValue";

    @BeforeEach
    void setUp() {
        // This is a good place to set up common mocks
        when(hazelcastInstance.getMap(anyString())).thenReturn(iMap);
    }

    @Test
    void testPut_Success() {

        // When
        hazelcastCacheService.put(MAP_NAME, TEST_KEY, TEST_VALUE);

        // Then
        // Verify that put() was called on the mock IMap
        verify(iMap, times(1)).put(TEST_KEY, TEST_VALUE);
    }

    @Test
    void testGet_Success() {
        // Given
        when(iMap.get(TEST_KEY)).thenReturn(TEST_VALUE);

        // When
        String result = hazelcastCacheService.get(MAP_NAME, TEST_KEY);

        // Then
        assertEquals(TEST_VALUE, result);
        verify(iMap, times(1)).get(TEST_KEY);
    }

    @Test
    void testGetAll_Success() {
        // Given
        Set<String> keys = Set.of(TEST_KEY);
        Map<String, String> expectedMap = Map.of(TEST_KEY, TEST_VALUE);
        when(iMap.getAll(keys)).thenReturn(expectedMap);

        // When
        Map<String, String> result = hazelcastCacheService.getAll(MAP_NAME, keys);

        // Then
        assertEquals(expectedMap, result);
        verify(iMap, times(1)).getAll(keys);
    }

    @Test
    void testRemove_Success() {
        // When
        hazelcastCacheService.remove(MAP_NAME, TEST_KEY);

        // Then
        verify(iMap, times(1)).remove(TEST_KEY);
    }

    @Test
    void testClear_Success() {
        // When
        hazelcastCacheService.clear(MAP_NAME);

        // Then
        verify(iMap, times(1)).clear();
    }

    @Test
    void testPutIfAbsent_Success() {
        // Given
        when(iMap.putIfAbsent(TEST_KEY, TEST_VALUE)).thenReturn(null);

        // When
        boolean result = hazelcastCacheService.putIfAbsent(MAP_NAME, TEST_KEY, TEST_VALUE);

        // Then
        assertTrue(result);
        verify(iMap, times(1)).putIfAbsent(TEST_KEY, TEST_VALUE);
    }

    @Test
    void testReplace_Success() {
        // Given
        when(iMap.replace(TEST_KEY, "oldValue", "newValue")).thenReturn(true);

        // When
        boolean result = hazelcastCacheService.replace(MAP_NAME, TEST_KEY, "oldValue", "newValue");

        // Then
        assertTrue(result);
        verify(iMap, times(1)).replace(TEST_KEY, "oldValue", "newValue");
    }
}