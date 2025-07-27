package net.therap.app.service;

/**
 * @author gazizafor
 * @since 21/7/25
 */

import net.therap.app.model.Instructor;
import net.therap.app.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service layer for managing Instructor entities.
 */
@Service
@Transactional(readOnly = true) // Default to read-only transactions for read methods
public class InstructorService {
    
    private final InstructorRepository instructorRepository;
    
    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }
    
    /**
     * Retrieves an instructor by their ID.
     * @param id The ID of the instructor.
     * @return An Optional containing the Instructor if found, or empty if not.
     */
    public Optional<Instructor> getInstructorById(Long id) {
        return instructorRepository.findById(id);
    }
    
    /**
     * Retrieves all instructors.
     * @return A list of all instructors.
     */
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }
    
    /**
     * Creates a new instructor.
     * @param instructor The Instructor entity to save.
     * @return The saved Instructor entity.
     */
    @Transactional // Override readOnly for write operations
    public Instructor createInstructor(Instructor instructor) {
        // Add any business logic here before saving, e.g., validation, setting defaults
        return instructorRepository.save(instructor);
    }
    
    /**
     * Updates an existing instructor.
     * @param id The ID of the instructor to update.
     * @param updatedInstructor The Instructor entity with updated details.
     * @return The updated Instructor entity.
     * @throws NoSuchElementException if the instructor with the given ID is not found.
     */
    @Transactional
    public Instructor updateInstructor(Long id, Instructor updatedInstructor) {
        Instructor existingInstructor = instructorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + id));
        
        // Update fields (only update allowed fields, e.g., name, not ID)
        existingInstructor.setName(updatedInstructor.getName());
        // existingInstructor.setEmail(updatedInstructor.getEmail()); // Assuming email is unique/mutable
        
        return instructorRepository.save(existingInstructor);
    }
    
    /**
     * Soft deletes an instructor by marking is_deleted to true.
     * @param id The ID of the instructor to delete.
     * @throws NoSuchElementException if the instructor with the given ID is not found.
     */
    @Transactional
    public void softDeleteInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + id));
        instructor.setDeleted(true);
        instructorRepository.save(instructor);
    }
}