package net.therap.app.service;

import net.therap.app.dto.ReorderDTO;
import net.therap.app.model.Content;
import net.therap.app.model.Module;
import net.therap.app.repository.CourseRepository;
import net.therap.app.repository.ModuleRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
@Transactional(readOnly = true)
public class ModuleService {
    
    private final ModuleRepository moduleRepository;
    private final ContentService contentService;
    private final MessageSource messageSource;
    
    public ModuleService(ModuleRepository moduleRepository, ContentService contentService, MessageSource messageSource) {
        this.moduleRepository = moduleRepository;
        this.contentService = contentService;
        this.messageSource = messageSource;
    }
    
    public List<Module> findAll() {
        return moduleRepository.findAll();
    }
    
    public Optional<Module> findById(long id) {
        return moduleRepository.findById(id);
    }
    
    public List<Module> findByCourseId(long id) {
        return moduleRepository.findByCourseId(id);
    }
    
    @Transactional
    public Module save(Module module) {
        return moduleRepository.save(module);
    }
    
    @Transactional
    public Module deleteById(long id) {
        Optional<Module> module = moduleRepository.findById(id);
        
        if (module.isPresent()) {
            module.get().setDeleted(true);
            return moduleRepository.save(module.get());
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.module", null, Locale.getDefault()));
    }
    
    public boolean isPublishable(Module module) {
        for (Content content : module.getContents()) {
            if (contentService.isPublishable(content)) {
                return true;
            }
        }
        
        return false;
    }
    
//    @Transactional
//    public List<Content> reorderContents(List<ReorderDTO> sortedContents) {
//        Map<Long,Module> moduleMap = new HashMap();
//
//        for (ReorderDTO dto : sortedModules) {
//            Optional<Module> moduleOptional = moduleService.findById(dto.getId());
//
//            if (moduleOptional.isEmpty()) {
//                throw new NoSuchElementException(messageSource.getMessage("not.found.module", null, Locale.getDefault()));
//            }
//
//            moduleMap.put(dto.getId(), moduleOptional.get());
//        }
//
//        validateModuleForOrdering(moduleMap);
//
//        sortedModules.forEach(dto -> {
//            moduleMap.get(dto.getId()).setOrderIndex(dto.getOrderIndex());
//        });
//
//        return moduleMap.values().stream().toList();
//    }
//
//    private void validateContentForReordering(Map<Long,Content> contentMap) throws BadRequestException {
//        Module module = contentMap.values().iterator().next();
//
//        if (contentMap.size() != module.getCourse().getModules().size()) {
//            throw new BadRequestException(messageSource.getMessage("validation.content.failed", null, Locale.getDefault()));
//        }
//
//        long courseId = module.getCourse().getId();
//
//        for (Content item : contentMap.values()) {
//            if (item.getCourse().getId() != courseId) {
//                throw new BadRequestException(messageSource.getMessage("validation.content.failed", null, Locale.getDefault()));
//            }
//        }
//    }
}