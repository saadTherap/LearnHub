package net.therap.learningProcessor.service;

import net.therap.learningProcessor.entity.UpdateInfo;
import org.springframework.stereotype.Service;

/**
 * @author tanvirhassan
 * @since 4/8/25
 */
@Service
public class UpdateInfoService {

    public void invalidateCache(UpdateInfo updateInfo) {
        System.out.println("invalidateCache from hazlecast - KafkaUpdateInfoService");
    }
}
