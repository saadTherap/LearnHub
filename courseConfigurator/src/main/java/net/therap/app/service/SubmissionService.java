package net.therap.app.service;

import net.therap.app.model.Module;
import net.therap.app.model.Submission;
import net.therap.app.repository.ModuleRepository;
import net.therap.app.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Transactional(readOnly = true)
@Service
public class SubmissionService {
    
    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
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
    public Submission createSubmission(Submission submission, Long moduleId) {
        Optional<Module> moduleOptional = moduleRepository.findById(moduleId);
        if (moduleOptional.isPresent()) {
            submission.getContent().setModule(moduleOptional.get());
            return submissionRepository.save(submission);
        }
        throw new RuntimeException("Module not found with ID: " + moduleId);
    }
    
    @Transactional
    public Submission updateSubmission(long id, Submission submissionDetails) {
        Optional<Submission> submissionOptional = submissionRepository.findById(id);
        if (submissionOptional.isPresent()) {
            Submission existingSubmission = submissionOptional.get();
            existingSubmission.getContent().setTitle(submissionDetails.getContent().getTitle());
            existingSubmission.setDescription(submissionDetails.getDescription());
            existingSubmission.setResourceLink(submissionDetails.getResourceLink());
            return submissionRepository.save(existingSubmission);
        }
        throw new RuntimeException("Submission not found with ID: " + id);
    }
    
    @Transactional
    public void deleteById(long id) {
        submissionRepository.deleteById(id);
    }
}