package net.therap.secureFileServer.mapper;

import net.therap.secureFileServer.dto.StoredFileDto;
import net.therap.secureFileServer.entity.StoredFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * @author avidewan
 * @since 7/22/25
 */
@Mapper(componentModel = "spring")
public interface StoredFileMapper {

    @Mapping(target = "downloadUrl", source = "formId", qualifiedByName = "toDownloadUrl")
    StoredFileDto toDto(StoredFile storedFile);

    @Named("toDownloadUrl")
    default String toDownloadUrl(String formId) {
        return "/api/secure-file-server/files/download?formId=" + formId;
    }
}