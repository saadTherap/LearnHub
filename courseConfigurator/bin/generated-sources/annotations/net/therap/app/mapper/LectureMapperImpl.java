package net.therap.app.mapper;

import javax.annotation.processing.Generated;
import net.therap.app.dto.LectureCatalogDTO;
import net.therap.app.model.Lecture;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:49+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class LectureMapperImpl implements LectureMapper {

    @Override
    public Lecture toLecture(LectureCatalogDTO lectureCatalogDTO) {
        if ( lectureCatalogDTO == null ) {
            return null;
        }

        Lecture lecture = new Lecture();

        lecture.setOrderIndex( lectureCatalogDTO.getOrderIndex() );
        lecture.setDescription( lectureCatalogDTO.getDescription() );
        lecture.setResourceLink( lectureCatalogDTO.getResourceLink() );
        lecture.setVideoUrl( lectureCatalogDTO.getVideoUrl() );

        return lecture;
    }

    @Override
    public LectureCatalogDTO toLectureCatalogDTO(Lecture lecture) {
        if ( lecture == null ) {
            return null;
        }

        LectureCatalogDTO lectureCatalogDTO = new LectureCatalogDTO();

        lectureCatalogDTO.setId( lecture.getId() );
        lectureCatalogDTO.setOrderIndex( lecture.getOrderIndex() );
        lectureCatalogDTO.setDescription( lecture.getDescription() );
        lectureCatalogDTO.setResourceLink( lecture.getResourceLink() );
        lectureCatalogDTO.setVideoUrl( lecture.getVideoUrl() );

        return lectureCatalogDTO;
    }

    @Override
    public void updateLectureFromLectureCatalogDto(LectureCatalogDTO lectureCatalogDTO, Lecture lecture) {
        if ( lectureCatalogDTO == null ) {
            return;
        }

        lecture.setOrderIndex( lectureCatalogDTO.getOrderIndex() );
        if ( lectureCatalogDTO.getDescription() != null ) {
            lecture.setDescription( lectureCatalogDTO.getDescription() );
        }
        if ( lectureCatalogDTO.getResourceLink() != null ) {
            lecture.setResourceLink( lectureCatalogDTO.getResourceLink() );
        }
        if ( lectureCatalogDTO.getVideoUrl() != null ) {
            lecture.setVideoUrl( lectureCatalogDTO.getVideoUrl() );
        }
    }
}
