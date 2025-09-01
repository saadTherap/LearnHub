package net.therap.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author gazizafor
 * @since 31/7/25
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
//        uses = {InstructorMappingHelper.class}
)
public interface ContentMapper {
    
//    ContentCatalogueDTO toContentCatalogueDTO(ContentRelease contentRelease);
//
//    Content toContent(ContentCatalogueDTO contentCatalogueDTO);
//
//    ContentReleaseDTO toContentReleaseDTO(ContentRelease contentRelease);
}