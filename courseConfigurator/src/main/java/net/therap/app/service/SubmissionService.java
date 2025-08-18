package net.therap.app.service;

import net.therap.app.model.Submission;
import net.therap.app.repository.ModuleRepository;
import net.therap.app.repository.SubmissionRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Transactional(readOnly = true)
@Service
public class SubmissionService {
    
    private final SubmissionRepository submissionRepository;
    
    private final ModuleRepository moduleRepository;
    private final MessageSource messageSource;
    
    public SubmissionService(SubmissionRepository submissionRepository, ModuleRepository moduleRepository, MessageSource messageSource) {
        this.submissionRepository = submissionRepository;
        this.moduleRepository = moduleRepository;
        this.messageSource = messageSource;
    }
    
    public List<Submission> findAll() {
        return submissionRepository.findAll();
    }
    
    public Optional<Submission> findById(long id) {
        return submissionRepository.findById(id);
    }
    
    @Transactional
    public Submission save(Submission submission) {
        return submissionRepository.save(submission);
    }
    
    @Transactional
    public Submission deleteById(long id) {
        Optional<Submission> submission = submissionRepository.findById(id);
        
        if (submission.isPresent()) {
            submission.get().setDeleted(true);
            return submissionRepository.save(submission.get());
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.submission", null, Locale.getDefault()));
    }
}