package net.therap.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.*;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.ModuleMapper;
import net.therap.app.model.Content;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.service.CourseService;
import net.therap.app.service.ModuleService;
import net.therap.app.validation.OnUpdate;
import net.therap.cache.support.HazelcastCacheService;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.therap.app.util.CollectionUtil.isValidOrderedList;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@RestController
@RequestMapping("/modules")
@Slf4j
public class ModuleController {
    
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
        List<ModuleDTO> cachedModules = hazelcastCacheService.get(CacheConstants.MODULES_BY_COURSE, courseId);
        if (cachedModules != null) {
            return ResponseEntity.ok(cachedModules);
        }

        List<Module> modules = moduleService.findByCourseId(courseId);
        List<ModuleDTO> moduleDTOs = modules.stream()
                .map(dtoHelper::toModuleDtoLazy)
                .collect(Collectors.toList());

        hazelcastCacheService.put(CacheConstants.MODULES_BY_COURSE, courseId, moduleDTOs);

        return ResponseEntity.ok(moduleDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable long id) {
        ModuleDTO cachedDto = hazelcastCacheService.get(CacheConstants.MODULES, id);
        if (cachedDto != null) {
            return ResponseEntity.ok(cachedDto);
        }

        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isPresent()) {
            Module module = moduleOptional.get();
            ModuleDTO dto = new ModuleDTO();
            BeanUtils.copyProperties(module, dto);

            hazelcastCacheService.put(CacheConstants.MODULES, id, dto);

            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<ModuleDTO> createModule(@RequestBody @Validated ModuleDTO moduleDTO, HttpServletRequest request) {
        log.info("Creating new module {}", moduleDTO);
        
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
        module.setOrderIndex(moduleService.getMaxOrderIndexOfModules(module.getCourse().getId()));
        
        log.info("Module from DTO: {}", module);
        Module savedModule = moduleService.save(module);
        
        return new ResponseEntity<>(moduleMapper.toModuleDTO(savedModule), HttpStatus.CREATED);
    }
    
    @PostMapping("/contents/reorder")
    public ResponseEntity<List<ContentCatalogueDTO>> reorderModules(@RequestBody @Validated(OnUpdate.class) List<ReorderDTO> contents) throws BadRequestException {
        log.info("[POST] /modules/contents/reorder\n{}", contents);
        
        if (!isValidOrderedList(contents)) {
            throw new BadRequestException(messageSource.getMessage("invalid.reorder", null, Locale.getDefault()));
        }
        
        log.info("passed validation");
        List<ReorderDTO> sortedContents = contents.stream()
                .sorted(Comparator.comparingLong(ReorderDTO::getOrderIndex))
                .toList();
        
        long newOrderIndex = 1;
        
        for (ReorderDTO module : sortedContents) {
            module.setOrderIndex(newOrderIndex++);
        }
        
        log.info("sort done!");
        List<Content> updatedContents = moduleService.reorderContents(sortedContents);
        
        return ResponseEntity.ok(updatedContents.stream().map(dtoHelper::toContentCatalogueDTO).toList());
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
    public ResponseEntity<ModuleDTO> deleteModule(@PathVariable long id) {
        return ResponseEntity.ok(dtoHelper.toModuleDtoLazy(moduleService.deleteById(id)));
    }
}