package net.therap.app.mapper;

import javax.annotation.processing.Generated;
import net.therap.app.dto.SubmissionCatalogueDTO;
import net.therap.app.model.Submission;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:49+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class SubmissionMapperImpl implements SubmissionMapper {

    @Override
    public Submission toSubmission(SubmissionCatalogueDTO submissionDTO) {
        if ( submissionDTO == null ) {
            return null;
        }

        Submission submission = new Submission();

        submission.setOrderIndex( submissionDTO.getOrderIndex() );
        submission.setDescription( submissionDTO.getDescription() );
        submission.setResourceLink( submissionDTO.getResourceLink() );

        return submission;
    }

    @Override
    public SubmissionCatalogueDTO toSubmissionCatalogDTO(Submission submission) {
        if ( submission == null ) {
            return null;
        }

        SubmissionCatalogueDTO submissionCatalogueDTO = new SubmissionCatalogueDTO();

        submissionCatalogueDTO.setId( submission.getId() );
        submissionCatalogueDTO.setOrderIndex( submission.getOrderIndex() );
        submissionCatalogueDTO.setDescription( submission.getDescription() );
        submissionCatalogueDTO.setResourceLink( submission.getResourceLink() );

        return submissionCatalogueDTO;
    }

    @Override
    public void updateSubmissionFromSubmissionCatalogDto(SubmissionCatalogueDTO submissionCatalogueDTO, Submission submission) {
        if ( submissionCatalogueDTO == null ) {
            return;
        }

        submission.setOrderIndex( submissionCatalogueDTO.getOrderIndex() );
        if ( submissionCatalogueDTO.getDescription() != null ) {
            submission.setDescription( submissionCatalogueDTO.getDescription() );
        }
        if ( submissionCatalogueDTO.getResourceLink() != null ) {
            submission.setResourceLink( submissionCatalogueDTO.getResourceLink() );
        }
    }
}
