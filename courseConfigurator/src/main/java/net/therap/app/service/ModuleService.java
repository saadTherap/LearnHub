package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.ReorderDTO;
import net.therap.app.model.Content;
import net.therap.app.model.Module;
import net.therap.app.repository.ModuleRepository;
import net.therap.cache.support.CacheInvalidationUtil;
import org.apache.coyote.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Objects.isNull;

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
    private final ContentReleaseService contentReleaseService;
    private final CacheInvalidationUtil cacheInvalidationUtil;
    private final AuthorizationService authorizationService;
    
    public ModuleService(ModuleRepository moduleRepository, ContentService contentService, MessageSource messageSource, ContentReleaseService contentReleaseService, CacheInvalidationUtil cacheInvalidationUtil, AuthorizationService authorizationService) {
        this.moduleRepository = moduleRepository;
        this.contentService = contentService;
        this.messageSource = messageSource;
        this.contentReleaseService = contentReleaseService;
        this.cacheInvalidationUtil = cacheInvalidationUtil;
        this.authorizationService = authorizationService;
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


    public boolean isPublishable(Module module) {
        for (Content content : module.getContents()) {
            if (contentService.isPublishable(content)) {
                return true;
            }
        }
        
        return false;
    }

    private void validateContentForReordering(Map<Long,Content> contentMap) throws BadRequestException {
        Content content = contentMap.values().iterator().next();
        long numberOfContentsInModule = 0;
        
        for (Content item : content.getModule().getContents()) {
            if (!item.isDeleted()) {
                numberOfContentsInModule++;
            }
        }
        
        if (contentMap.size() != numberOfContentsInModule) {
            throw new BadRequestException(messageSource.getMessage("validation.content.reorder.failed", null, Locale.getDefault()));
        }

        long moduleId = content.getModule().getId();

        for (Content item : contentMap.values()) {
            if (item.getModule().getId() != moduleId) {
                throw new BadRequestException(messageSource.getMessage("validation.content.reorder.failed", null, Locale.getDefault()));
            }
        }
    }
    
    public long getMaxOrderIndexOfModules(long courseId) {
        return moduleRepository.findMaxOrderIndexOfModules(courseId);
    }
    
    @Transactional
    public List<Content> reorderContents(List<ReorderDTO> sortedContents) throws BadRequestException {
        Map<Long, Content> contentMap = new HashMap();
        
        for (ReorderDTO dto : sortedContents) {
            Optional<Content> contentOptional = contentService.findById(dto.getId());
            
            if (contentOptional.isEmpty()) {
                throw new NoSuchElementException(messageSource.getMessage("not.found.content", null, Locale.getDefault()));
            }
            
            contentMap.put(dto.getId(), contentOptional.get());
        }
        
        validateContentForReordering(contentMap);
        
        for (ReorderDTO dto : sortedContents) {
            Content content = contentMap.get(dto.getId());
            
            if (isNull(content.getCurrentContentRelease())) {
                throw new BadRequestException(messageSource.getMessage("reorder.deleted.content", null, Locale.getDefault()));
            }
            
            content.getCurrentContentRelease().setOrderIndex(dto.getOrderIndex());
            contentReleaseService.save(content.getCurrentContentRelease());
        }
        
        return contentMap.values().stream().toList();
    }
    
    @Transactional
    public Module save(Module module) {
        
        Module saved = moduleRepository.save(module);
        
        cacheInvalidationUtil.invalidateCachesAfterCommit(
                saved.getId(),
                CacheConstants.MODULES
        );
        cacheInvalidationUtil.invalidateCachesAfterCommit(
                saved.getCourse().getId(),
                CacheConstants.MODULES_BY_COURSE, CacheConstants.COURSES, CacheConstants.COURSE_CATALOG, CacheConstants.COURSE_CATALOG_PUBLIC
        );
        
        return saved;
    }
    
    @Transactional
    public Module deleteById(long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        messageSource.getMessage("not.found.module", null, Locale.getDefault())
                ));

        Long courseId = module.getCourse().getId();
        
        module.setDeleted(true);
        Module saved = moduleRepository.save(module);
        
        cacheInvalidationUtil.invalidateCachesAfterCommit(
                id, CacheConstants.MODULES
        );
        cacheInvalidationUtil.invalidateCachesAfterCommit(
                courseId, CacheConstants.MODULES_BY_COURSE, CacheConstants.COURSES, CacheConstants.COURSE_CATALOG, CacheConstants.COURSE_CATALOG_PUBLIC
        );
        
        return saved;
    }
}