package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.model.Instructor;
import net.therap.app.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    @Autowired
    private HazelcastCacheService hazelcastCacheService;
    
    public Optional<Instructor> getInstructorById(Long id) {
        return instructorRepository.findById(id);
    }
    
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    @Transactional
    public Instructor createInstructor(Instructor instructor) {
        Instructor savedInstructor = instructorRepository.save(instructor);
        invalidateCacheAfterCommit(savedInstructor.getId(), CacheConstants.INSTRUCTORS, CacheConstants.INSTRUCTOR_CATALOG);
        return savedInstructor;
    }

    @Transactional
    public Instructor updateInstructor(Instructor updatedInstructor) {
        Instructor savedInstructor = instructorRepository.save(updatedInstructor);
        invalidateCacheAfterCommit(savedInstructor.getId(), CacheConstants.INSTRUCTORS,  CacheConstants.INSTRUCTOR_CATALOG);
        return savedInstructor;
    }

    @Transactional
    public void deleteInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with ID: " + id));
        instructor.setDeleted(true);
        Instructor deletedInstructor = instructorRepository.save(instructor);
        invalidateCacheAfterCommit(deletedInstructor.getId(), CacheConstants.INSTRUCTORS,  CacheConstants.INSTRUCTOR_CATALOG);
    }

    private void invalidateCacheAfterCommit(Long id, String... mapNames) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (String mapName : mapNames) {
                    hazelcastCacheService.remove(mapName, id);
                }
            }
        });
    }

}