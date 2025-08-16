package net.therap.secureFileServer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author avidewan
 * @since 8/14/25
 */
@Data
@AllArgsConstructor
public class FileMetaDataDto {

    private Long userId;
    private String userRole;
    private Long contextId;
}