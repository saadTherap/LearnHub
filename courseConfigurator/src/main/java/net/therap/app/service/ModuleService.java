package net.therap.app.service;

import net.therap.app.model.Content;
import net.therap.app.model.Module;
import net.therap.app.repository.CourseRepository;
import net.therap.app.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
@Transactional(readOnly = true)
public class ModuleService {
    
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private ContentService contentService;
    
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
    public void deleteById(long id) {
        moduleRepository.deleteById(id);
    }
    
    public boolean isPublishable(Module module) {
        for (Content content : module.getContents()) {
            if (contentService.isPublishable(content)) {
                return true;
            }
        }
        
        return false;
    }
}