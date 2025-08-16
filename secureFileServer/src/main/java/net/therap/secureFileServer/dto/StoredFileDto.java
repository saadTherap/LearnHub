package net.therap.secureFileServer.dto;

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

    private Long id;

    private String originalFilename;

    private String contentType;

    private String downloadUrl;

    private LocalDateTime uploadTime;

    private Long uploaderId;

    private String uploaderRole;

    private Long contextId;

    private String signature;
}