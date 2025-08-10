package net.therap.secureFileServer.mapper;

import javax.annotation.processing.Generated;
import net.therap.secureFileServer.dto.StoredFileDto;
import net.therap.secureFileServer.entity.StoredFile;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:51+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class StoredFileMapperImpl implements StoredFileMapper {

    @Override
    public StoredFileDto toDto(StoredFile storedFile) {
        if ( storedFile == null ) {
            return null;
        }

        StoredFileDto.StoredFileDtoBuilder storedFileDto = StoredFileDto.builder();

        storedFileDto.downloadUrl( toDownloadUrl( storedFile.getId() ) );
        storedFileDto.contentType( storedFile.getContentType() );
        storedFileDto.id( storedFile.getId() );
        storedFileDto.originalFilename( storedFile.getOriginalFilename() );
        storedFileDto.uploadTime( storedFile.getUploadTime() );

        return storedFileDto.build();
    }
}
