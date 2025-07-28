package net.therap.app.service;

import net.therap.app.model.Instructor;
import net.therap.app.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Service
@Transactional(readOnly = true)
public class InstructorService {
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    public Optional<Instructor> getInstructorById(Long id) {
        return instructorRepository.findById(id);
    }
    
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }
    
    @Transactional
    public Instructor createInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }
    
    @Transactional
    public Instructor updateInstructor(Instructor updatedInstructor) {
        return instructorRepository.save(updatedInstructor);
    }
    
    @Transactional
    public void softDeleteInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + id));
        instructor.setDeleted(true);
        instructorRepository.save(instructor);
    }
}