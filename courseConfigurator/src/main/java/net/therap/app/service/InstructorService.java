package net.therap.app.service;

import net.therap.app.constants.CacheConstants;
import net.therap.app.model.Instructor;
import net.therap.app.repository.InstructorRepository;
import net.therap.cache.support.CacheInvalidationUtil;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Service
@Transactional(readOnly = true)
public class InstructorService {
    
    private final CacheInvalidationUtil cacheInvalidationUtil;
    private final InstructorRepository instructorRepository;
    private final MessageSource messageSource;
    
    public InstructorService(InstructorRepository instructorRepository, MessageSource messageSource, CacheInvalidationUtil cacheInvalidationUtil) {
        this.instructorRepository = instructorRepository;
        this.messageSource = messageSource;
        this.cacheInvalidationUtil = cacheInvalidationUtil;
    }
    
    public Optional<Instructor> getInstructorById(Long id) {
        return instructorRepository.findById(id);
    }
    
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }
    
    public Optional<Instructor> getByEmail(String email) {
        return instructorRepository.findByEmail(email);
    }

    @Transactional
    public Instructor createInstructor(Instructor instructor) {
        Instructor savedInstructor = instructorRepository.save(instructor);
        cacheInvalidationUtil.invalidateCachesAfterCommit(String.valueOf(savedInstructor.getId()), CacheConstants.INSTRUCTORS, CacheConstants.INSTRUCTOR_CATALOG);

        return savedInstructor;
    }

    @Transactional
    public Instructor updateInstructor(Instructor updatedInstructor) {
        Instructor savedInstructor = instructorRepository.save(updatedInstructor);
        cacheInvalidationUtil.invalidateCachesAfterCommit(String.valueOf(savedInstructor.getId()), CacheConstants.INSTRUCTORS,  CacheConstants.INSTRUCTOR_CATALOG);

        return savedInstructor;
    }

    @Transactional
    public Instructor deleteById(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(messageSource.getMessage("not.found.instructor", null, Locale.getDefault())));
        instructor.setDeleted(true);
        Instructor deletedInstructor = instructorRepository.save(instructor);
        cacheInvalidationUtil.invalidateCachesAfterCommit(String.valueOf(deletedInstructor.getId()), CacheConstants.INSTRUCTORS,  CacheConstants.INSTRUCTOR_CATALOG);

        return deletedInstructor;
    }
    
    public boolean isEmailAlreadyInUse(String email) {
        return instructorRepository.existsByEmail(email);
    }
}