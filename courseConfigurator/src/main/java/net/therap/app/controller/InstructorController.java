package net.therap.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.therap.app.dto.InstructorDTO; // Import InstructorDTO
import net.therap.app.mapper.InstructorMapper;
import net.therap.app.model.Instructor;
import net.therap.app.service.InstructorService;
import net.therap.app.helper.DtoHelper; // Import DtoHelper
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for managing Instructor data.
 * @author gazizafor
 * @since 21/7/25
 */
@RestController
@RequestMapping("/api/instructors")
public class InstructorController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final InstructorService instructorService;
    private final DtoHelper dtoHelper;
    private final InstructorMapper instructorMapper;
    
    @Autowired
    public InstructorController(InstructorService instructorService, DtoHelper dtoHelper, ObjectMapper objectMapper, InstructorMapper instructorMapper) { // Constructor injection
        this.instructorService = instructorService;
        this.dtoHelper = dtoHelper;
        this.objectMapper = objectMapper;
        this.instructorMapper = instructorMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorDTO> getInstructorById(@PathVariable Long id) {
        return instructorService.getInstructorById(id)
                .map(dtoHelper::toInstructorDTO) 
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<InstructorDTO>> getAllInstructors() {
        List<Instructor> instructors = instructorService.getAllInstructors();
        List<InstructorDTO> instructorDTOs = instructors.stream()
                .map(dtoHelper::toInstructorDTO) 
                .collect(Collectors.toList());
        return ResponseEntity.ok(instructorDTOs);
    }
    
    @PostMapping
    public ResponseEntity<InstructorDTO> createInstructor(@RequestBody @Validated(OnCreate.class) InstructorDTO instructorDTO) {
        Instructor instructorToCreate = instructorMapper.toInstructor(instructorDTO);
        instructorToCreate.setId(0);
        Instructor createdInstructor = instructorService.createInstructor(instructorToCreate);
        
        return new ResponseEntity<>(dtoHelper.toInstructorDTO(createdInstructor), HttpStatus.CREATED);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<InstructorDTO> updateInstructor(@PathVariable long id, @RequestBody @Validated(OnUpdate.class) InstructorDTO instructorDTO) {
        Optional<Instructor> instructorToUpdate = instructorService.getInstructorById(id);
        
        if (instructorToUpdate.isPresent()) {
            if (instructorDTO.getId() != id && instructorDTO.getId() != 0) {
                return ResponseEntity.badRequest().build();
            }
            
            instructorDTO.setId(instructorToUpdate.get().getId());
            instructorMapper.updateInstructorFromDto(instructorDTO, instructorToUpdate.get());
            Instructor updatedInstructor = instructorService.updateInstructor(instructorToUpdate.get());
            
            return new ResponseEntity<>(dtoHelper.toInstructorDTO(updatedInstructor), HttpStatus.OK);
            
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteInstructor(@PathVariable Long id) {
        try {
            instructorService.deleteInstructor(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}