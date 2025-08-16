package net.therap.secureFileServer.mapper;

import net.therap.secureFileServer.dto.StoredFileDto;
import net.therap.secureFileServer.entity.primary.StoredFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * @author avidewan
 * @since 7/22/25
 */

@Mapper(componentModel = "spring")
public interface StoredFileMapper {

    @Mapping(target = "downloadUrl", source = "id", qualifiedByName = "toDownloadUrl")
    StoredFileDto toDto(StoredFile storedFile);

    @Named("toDownloadUrl")
    default String toDownloadUrl(Long id) {
        return "/api/files/" + id;
    }
}