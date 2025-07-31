package net.therap.app.mapper;

import net.therap.app.dto.SubmissionCatalogueDTO;
import net.therap.app.dto.SubmissionDTO;
import net.therap.app.model.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author gazizafor
 * @since 31/7/25
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubmissionMapper {
    
    @Mapping(target = "id", ignore = true)
    Submission toSubmission(SubmissionCatalogueDTO submissionDTO);
    
    SubmissionCatalogueDTO toSubmissionCatalogDTO(Submission submission);
    
    @Mapping(target = "id", ignore = true)
    void updateSubmissionFromSubmissionCatalogDto(SubmissionCatalogueDTO submissionCatalogueDTO,
                                                  @MappingTarget SubmissionDTO submissionDTO);
}