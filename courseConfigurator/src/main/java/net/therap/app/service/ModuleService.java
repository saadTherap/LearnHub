package net.therap.app.service;

import net.therap.app.model.Module;
import net.therap.app.repository.CourseRepository;
import net.therap.app.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
public class ModuleService {
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    public List<Module> findAll() {
        return moduleRepository.findAll();
    }
    
    public Optional<Module> findById(long id) {
        return moduleRepository.findById(id);
    }
    
    public List<Module> findByCourseId(long id) {
        return moduleRepository.findByCourseId(id);
    }
    
    public Module save(Module module) {
        return moduleRepository.save(module);
    }
    
    public void deleteById(long id) {
        moduleRepository.deleteById(id);
    }
}