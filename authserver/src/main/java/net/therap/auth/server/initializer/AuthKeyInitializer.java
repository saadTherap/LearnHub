package net.therap.auth.server.initializer;

import lombok.RequiredArgsConstructor;
import net.therap.auth.server.respository.AuthKeyRepository;
import net.therap.auth.server.service.AuthKeyService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author apurboturjo
 * @since 9/8/25
 */
@Component
@RequiredArgsConstructor
public class AuthKeyInitializer {
    
    private final AuthKeyRepository authKeyRepository;
    private final AuthKeyService authKeyService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (authKeyRepository.count() == 0) {
            authKeyService.generateAndSaveKeyPair();
        }
    }
}