package net.therap.app.mapper;

import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.dto.ContentReleaseDTO;
import net.therap.app.helper.InstructorMappingHelper;
import net.therap.app.model.Content;
import net.therap.app.model.ContentRelease;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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