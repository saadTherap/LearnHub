package net.therap.app.mapper;

import javax.annotation.processing.Generated;
import net.therap.app.dto.ModuleDTO;
import net.therap.app.helper.CourseMappingHelper;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:49+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ModuleMapperImpl implements ModuleMapper {

    @Autowired
    private CourseMappingHelper courseMappingHelper;

    @Override
    public Module toModule(ModuleDTO moduleDTO) {
        if ( moduleDTO == null ) {
            return null;
        }

        Module module = new Module();

        module.setCourse( courseMappingHelper.map( moduleDTO.getCourseId() ) );
        module.setOrderIndex( moduleDTO.getOrderIndex() );
        module.setTitle( moduleDTO.getTitle() );

        return module;
    }

    @Override
    public ModuleDTO toModuleDTO(Module module) {
        if ( module == null ) {
            return null;
        }

        ModuleDTO moduleDTO = new ModuleDTO();

        moduleDTO.setCourseId( moduleCourseId( module ) );
        moduleDTO.setId( module.getId() );
        moduleDTO.setOrderIndex( module.getOrderIndex() );
        moduleDTO.setTitle( module.getTitle() );

        return moduleDTO;
    }

    @Override
    public void updateModuleFromDto(ModuleDTO moduleDTO, Module module) {
        if ( moduleDTO == null ) {
            return;
        }

        module.setCourse( courseMappingHelper.map( moduleDTO.getCourseId() ) );
        module.setOrderIndex( moduleDTO.getOrderIndex() );
        if ( moduleDTO.getTitle() != null ) {
            module.setTitle( moduleDTO.getTitle() );
        }
    }

    private long moduleCourseId(Module module) {
        if ( module == null ) {
            return 0L;
        }
        Course course = module.getCourse();
        if ( course == null ) {
            return 0L;
        }
        long id = course.getId();
        return id;
    }
}
