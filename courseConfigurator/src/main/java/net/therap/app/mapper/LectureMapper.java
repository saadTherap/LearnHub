package net.therap.app.mapper;

import net.therap.app.dto.LectureCatalogDTO;
import net.therap.app.helper.InstructorMappingHelper;
import net.therap.app.model.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @author gazizafor
 * @since 31/7/25
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface LectureMapper {
    
    @Mapping(target = "id", ignore = true)
    Lecture toLecture(LectureCatalogDTO lectureCatalogDTO);
    
    LectureCatalogDTO toLectureCatalogDTO(Lecture lecture);
    
    @Mapping(target = "id", ignore = true)
    void updateLectureFromLectureCatalogDto(LectureCatalogDTO lectureCatalogDTO, @MappingTarget Lecture lecture);
}