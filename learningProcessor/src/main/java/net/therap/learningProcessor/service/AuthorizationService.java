package net.therap.learningProcessor.service;

import net.therap.learningProcessor.eum.AccessLevel;

import java.util.Map;

/**
 * @author avidewan
 * @since 8/13/25
 */
public interface AuthorizationService {

    void authorize(AccessLevel level);

    void authorize(AccessLevel level, Map<String, Object> params);
}
