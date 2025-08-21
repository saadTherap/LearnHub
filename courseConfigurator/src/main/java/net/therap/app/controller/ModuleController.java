package net.therap.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.*;
import net.therap.app.helper.AuthorizationService;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.ModuleMapper;
import net.therap.app.model.Content;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.service.ContentService;
import net.therap.app.service.CourseService;
import net.therap.app.service.ModuleService;
import net.therap.app.validation.OnUpdate;
import net.therap.cache.support.HazelcastCacheService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    private final AuthorizationService authorizationService;
    private final ContentService contentService;
    
    @Autowired
    public ModuleController(CourseService courseService, ModuleMapper moduleMapper, DtoHelper dtoHelper,
                            ModuleService moduleService, MessageSource messageSource, HazelcastCacheService hazelcastCacheService, AuthorizationService authorizationService, ContentService contentService) {
        this.courseService = courseService;
        this.moduleMapper = moduleMapper;
        this.dtoHelper = dtoHelper;
        this.moduleService = moduleService;
        this.messageSource = messageSource;
        this.hazelcastCacheService = hazelcastCacheService;
        this.authorizationService = authorizationService;
        this.contentService = contentService;
    }
    
    @GetMapping
    public ResponseEntity<List<ModuleDTO>> getAllModules(HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /modules");
        authorizationService.authorize(AuthorizationLevel.ADMIN, null, request);
        List<Module> modules = moduleService.findAll();
        List<ModuleDTO> moduleDTOs = modules.stream().map(dtoHelper::toModuleDTO).collect(Collectors.toList());
        
        return ResponseEntity.ok(moduleDTOs);
    }
    
    @GetMapping("/byCourse/{courseId}")
    public ResponseEntity<List<ModuleDTO>> getModulesByCourse(@PathVariable long courseId, HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /modules/byCourse/{}", courseId);
        List<ModuleDTO> cachedModules = hazelcastCacheService.get(CacheConstants.MODULES_BY_COURSE, courseId);
        
        if (cachedModules != null) {
            authorizationService.authorize(AuthorizationLevel.OWNER, cachedModules, request);
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
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable long id, HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /modules/{}", id);
        ModuleDTO cachedDto = hazelcastCacheService.get(CacheConstants.MODULES, id);
        
        if (cachedDto != null) {
            authorizationService.authorize(AuthorizationLevel.OWNER, cachedDto, request);
            return ResponseEntity.ok(cachedDto);
        }

        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isPresent()) {
            authorizationService.authorize(AuthorizationLevel.OWNER, moduleOptional.get(), request);
            Module module = moduleOptional.get();
            ModuleDTO dto = new ModuleDTO();
            BeanUtils.copyProperties(module, dto);

            hazelcastCacheService.put(CacheConstants.MODULES, id, dto);

            return ResponseEntity.ok(dto);
        }

        throw new NoSuchElementException(messageSource.getMessage("not.found.module",  null, request.getLocale()));
    }
    
    @PostMapping
    public ResponseEntity<ModuleDTO> createModule(@RequestBody @Validated ModuleDTO moduleDTO,
                                                  HttpServletRequest request) throws BadRequestException {
        
        log.info("[POST] /modules\nRequestBody:\n{}", moduleDTO);
        
        if (moduleDTO.getId() != 0) {
            moduleDTO.setId(0);
        }
        
        Optional<Course> courseOptional = courseService.findById(moduleDTO.getCourseId());
        
        if (courseOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.course",  null, request.getLocale()));
        }
        
        authorizationService.authorize(AuthorizationLevel.OWNER, courseOptional.get(), request);
        Module module = moduleMapper.toModule(moduleDTO);
        module.setOrderIndex(moduleService.getMaxOrderIndexOfModules(module.getCourse().getId()) + 1);
        Module savedModule = moduleService.save(module);
        
        return new ResponseEntity<>(moduleMapper.toModuleDTO(savedModule), HttpStatus.CREATED);
    }
    
    @PostMapping("/contents/reorder")
    public ResponseEntity<List<ContentCatalogueDTO>> reorderModules(@RequestBody @Validated(OnUpdate.class) List<ReorderDTO> contents,
                                                                    HttpServletRequest request) throws BadRequestException {
        
        log.info("[POST] /modules/contents/reorder\nRequest body:\n{}", contents);
        
        if (!isValidOrderedList(contents)) {
            throw new BadRequestException(messageSource.getMessage("invalid.reorder", null, Locale.getDefault()));
        }
        
        Optional<Content> contentOptional = contentService.findById(contents.getFirst().getId());
        
        if (contentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
        }
        
        authorizationService.authorize(AuthorizationLevel.OWNER, contentOptional.get(), request);
        
        List<ReorderDTO> sortedContents = contents.stream().sorted(Comparator.comparingLong(ReorderDTO::getOrderIndex))
                .toList();
        
        long newOrderIndex = 1;
        
        for (ReorderDTO module : sortedContents) {
            module.setOrderIndex(newOrderIndex++);
        }
        
        List<Content> updatedContents = moduleService.reorderContents(sortedContents);
        
        return ResponseEntity.ok(updatedContents.stream().map(dtoHelper::toContentCatalogueDTO).toList());
    }
    
    
    @PatchMapping("/{id}")
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable long id, @RequestBody @Validated ModuleDTO moduleDTO,
                                                  HttpServletRequest request) throws BadRequestException {
        
        log.info("[PATCH] /modules/{}\nRequest Body:\n{}", id,  moduleDTO);
        Optional<Module> moduleOptional = moduleService.findById(id);
        
        if (moduleOptional.isPresent()) {
            authorizationService.authorize(AuthorizationLevel.OWNER, moduleOptional.get(), request);
            Module existingModule = moduleOptional.get();
            existingModule.setTitle(moduleDTO.getTitle());
            Module updatedModule = moduleService.save(existingModule);
            ModuleDTO responseDTO = moduleMapper.toModuleDTO(updatedModule);
            
            return ResponseEntity.ok(responseDTO);
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.module", null, Locale.getDefault()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity deleteModule(@PathVariable long id, HttpServletRequest request) throws BadRequestException {
        log.info("[DELETE] /modules/{}", id);
        Optional<Module> moduleOptional = moduleService.findById(id);
        
        if (moduleOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.module", null, Locale.getDefault()));
        }
        
        authorizationService.authorize(AuthorizationLevel.OWNER, moduleOptional.get(), request);
        
        return ResponseEntity.noContent().build();
    }
}