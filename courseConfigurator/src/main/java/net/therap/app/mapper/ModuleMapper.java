package net.therap.app.mapper;

import net.therap.app.dto.ModuleDTO;
import net.therap.app.helper.CourseMappingHelper;
import net.therap.app.model.Module;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
* @author gazizafor
* @since 30/7/25
*/
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CourseMappingHelper.class}
)
public interface ModuleMapper {
    
    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "contents", ignore = true)
    @Mapping(target = "course", source = "courseId")
    @Mapping(target = "orderIndex", ignore = true)
    Module toModule(ModuleDTO moduleDTO);
    
    @Mapping(target = "contents", ignore = true)
    @Mapping(target = "courseId", source = "course.id")
    ModuleDTO toModuleDTO(Module module);
    
    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "contents", ignore = true)
    @Mapping(target = "course", source = "courseId")
    void updateModuleFromDto(ModuleDTO moduleDTO, @MappingTarget Module module);
}