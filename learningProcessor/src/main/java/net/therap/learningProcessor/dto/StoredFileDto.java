package net.therap.learningProcessor.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author avidewan
 * @since 7/22/25
 */
@Data
@Builder
public class StoredFileDto {

    private String formId;

    private String originalFilename;

    private String contentType;

    private String downloadUrl;

    private LocalDateTime uploadTime;

    private String uploaderEmail;
}