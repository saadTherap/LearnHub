package net.therap.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.therap.app.dto.ErrorResponse;
import net.therap.app.dto.ModuleDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.ModuleMapper;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.service.CourseService;
import net.therap.app.service.HazelcastCacheService;
import net.therap.app.service.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@RestController
@RequestMapping("/api/modules")
public class ModuleController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ModuleService moduleService;
    private final DtoHelper dtoHelper;
    private final ModuleMapper moduleMapper;
    private final CourseService courseService;
    private final MessageSource messageSource;
    private final HazelcastCacheService hazelcastCacheService;
    
    @Autowired
    public ModuleController(CourseService courseService, ModuleMapper moduleMapper, DtoHelper dtoHelper,
                            ModuleService moduleService, MessageSource messageSource, HazelcastCacheService hazelcastCacheService) {
        this.courseService = courseService;
        this.moduleMapper = moduleMapper;
        this.dtoHelper = dtoHelper;
        this.moduleService = moduleService;
        this.messageSource = messageSource;
        this.hazelcastCacheService = hazelcastCacheService;
    }
    
    @GetMapping
    public ResponseEntity<List<ModuleDTO>> getAllModules() {
        List<Module> modules = moduleService.findAll();
        List<ModuleDTO> moduleDTOs =
                modules.stream().map(dtoHelper::toModuleDTO).collect(Collectors.toList());
        
        return ResponseEntity.ok(moduleDTOs);
    }
    
    @GetMapping("/byCourse/{courseId}")
    public ResponseEntity<List<ModuleDTO>> getModulesByCourse(@PathVariable long courseId) {
        List<Module> modules = moduleService.findByCourseId(courseId);
        List<ModuleDTO> moduleDTOs =
                modules.stream().map(dtoHelper::toModuleDtoLazy).collect(Collectors.toList());
        
        return ResponseEntity.ok(moduleDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable long id) {
        Optional<Module> moduleOptional = moduleService.findById(id);
        
        return moduleOptional.map(module -> {
            ModuleDTO dto = new ModuleDTO();
            BeanUtils.copyProperties(module, dto);
            
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<ModuleDTO> createModule(@RequestBody @Validated ModuleDTO moduleDTO, HttpServletRequest request) {
        logger.info("Creating new module {}", moduleDTO);
        
        if (moduleDTO.getId() != 0) {
            moduleDTO.setId(0);
        }
        
        Optional<Course> courseOptional = courseService.findById(moduleDTO.getCourseId());
        
        if (courseOptional.isEmpty()) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, messageSource.getMessage("error.course" +
                                                                                                               ".not" +
                                                                                                               ".found", null, request.getLocale()), request.getRequestURI()), HttpStatus.BAD_REQUEST);
        }
        
        Module module = moduleMapper.toModule(moduleDTO);
        
        logger.info("Module from DTO: {}", module);
        Module savedModule = moduleService.save(module);
        
        return new ResponseEntity<>(moduleMapper.toModuleDTO(savedModule), HttpStatus.CREATED);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable long id, @RequestBody @Validated ModuleDTO moduleDTO) {
        Optional<Module> moduleOptional = moduleService.findById(id);
        
        if (moduleOptional.isPresent()) {
            Module existingModule = moduleOptional.get();
            existingModule.setTitle(moduleDTO.getTitle());
            Module updatedModule = moduleService.save(existingModule);
            ModuleDTO responseDTO = moduleMapper.toModuleDTO(updatedModule);
            
            return ResponseEntity.ok(responseDTO);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable long id) {
        if (moduleService.findById(id).isPresent()) {
            moduleService.deleteById(id);
        
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }
}