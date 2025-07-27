package net.therap.app.controller;

import net.therap.app.dto.ModuleDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Module;
import net.therap.app.service.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    
    @Autowired
    private ModuleService moduleService;
    
    @Autowired
    private DtoHelper dtoHelper;
    
    @GetMapping
    public ResponseEntity<List<ModuleDTO>> getAllModules() {
        List<Module> modules = moduleService.findAll();
        List<ModuleDTO> moduleDTOs = modules.stream()
                .map(module -> dtoHelper.toModuleDTO(module))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(moduleDTOs);
    }
    
    @GetMapping("/byCourse/{courseId}")
    public ResponseEntity<List<ModuleDTO>> getModulesByCourse(@PathVariable long courseId) {
        List<Module> modules = moduleService.findByCourseId(courseId);
        List<ModuleDTO> moduleDTOs = modules.stream()
                .map(module -> dtoHelper.toModuleDtoLazy(module))
                .collect(Collectors.toList());
        
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
    public ResponseEntity<ModuleDTO> createModule(@RequestBody ModuleDTO moduleDTO) {
        Module module = new Module();
        BeanUtils.copyProperties(moduleDTO, module);
        Module savedModule = moduleService.save(module);
        ModuleDTO responseDTO = new ModuleDTO();
        BeanUtils.copyProperties(savedModule, responseDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ModuleDTO> updateModule(@PathVariable long id, @RequestBody ModuleDTO moduleDTO) {
        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isPresent()) {
            Module existingModule = moduleOptional.get();
            existingModule.setTitle(moduleDTO.getTitle());
            Module updatedModule = moduleService.save(existingModule);
            ModuleDTO responseDTO = new ModuleDTO();
            BeanUtils.copyProperties(updatedModule, responseDTO);
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