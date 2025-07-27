package net.therap.app.controller;

import net.therap.app.dto.InstructorDTO; // Import InstructorDTO
import net.therap.app.model.Instructor;
import net.therap.app.service.InstructorService;
import net.therap.app.helper.DtoHelper; // Import DtoHelper
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * REST Controller for managing Instructor data.
 * @author gazizafor
 * @since 21/7/25
 */
@RestController
@RequestMapping("/api/instructors") // Base path for instructor-related APIs
public class InstructorController {
    
    private final InstructorService instructorService;
    private final DtoHelper dtoHelper; // Inject DtoHelper
    
    public InstructorController(InstructorService instructorService, DtoHelper dtoHelper) { // Constructor injection
        this.instructorService = instructorService;
        this.dtoHelper = dtoHelper;
    }
    
    // API to fetch a single instructor by ID
    // Example: GET /api/instructors/1
    @GetMapping("/{id}")
    public ResponseEntity<InstructorDTO> getInstructorById(@PathVariable Long id) {
        return instructorService.getInstructorById(id)
                .map(dtoHelper::toInstructorDTO) // <<< CHANGED: Use DtoHelper
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // API to fetch all instructors
    // Example: GET /api/instructors
    @GetMapping
    public ResponseEntity<List<InstructorDTO>> getAllInstructors() {
        List<Instructor> instructors = instructorService.getAllInstructors();
        List<InstructorDTO> instructorDTOs = instructors.stream()
                .map(dtoHelper::toInstructorDTO) // <<< CHANGED: Use DtoHelper
                .collect(Collectors.toList());
        return ResponseEntity.ok(instructorDTOs);
    }
    
    // API to create a new instructor
    // Example: POST /api/instructors
    @PostMapping
    public ResponseEntity<InstructorDTO> createInstructor(@RequestBody InstructorDTO instructorDTO) {
        // --- DTO to Entity Mapping for Creation ---
        Instructor instructorToCreate = new Instructor();
        // BeanUtils.copyProperties can be used for simple fields, but be careful with IDs/timestamps
        instructorToCreate.setName(instructorDTO.getName());
        // instructorToCreate.setEmail(instructorDTO.getEmail()); // Set other fields if present in DTO
        
        Instructor createdInstructor = instructorService.createInstructor(instructorToCreate);
        return new ResponseEntity<>(dtoHelper.toInstructorDTO(createdInstructor), HttpStatus.CREATED); // <<< CHANGED: Use DtoHelper
    }
    
    // API to update an instructor
    // Example: PUT /api/instructors/1
    @PutMapping("/{id}")
    public ResponseEntity<InstructorDTO> updateInstructor(@PathVariable Long id, @RequestBody InstructorDTO instructorDTO) {
        try {
            // --- DTO to Entity Mapping for Update ---
            // Fetch existing entity and update its fields from DTO
            // Instructor existingInstructor = instructorService.getInstructorById(id)
            //                                   .orElseThrow(() -> new NoSuchElementException("Instructor not found"));
            // existingInstructor.setName(instructorDTO.getName());
            // Instructor updatedInstructor = instructorService.updateInstructor(id, existingInstructor);
            
            // Using the simplified update from previous example:
            Instructor instructorToUpdate = new Instructor();
            instructorToUpdate.setName(instructorDTO.getName());
            
            Instructor updatedInstructor = instructorService.updateInstructor(id, instructorToUpdate);
            return ResponseEntity.ok(dtoHelper.toInstructorDTO(updatedInstructor)); // <<< CHANGED: Use DtoHelper
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // API to soft delete an instructor
    // Example: DELETE /api/instructors/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteInstructor(@PathVariable Long id) {
        try {
            instructorService.softDeleteInstructor(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}