package net.therap.app.controller;

import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.dto.StudentSubmissionDTO;
import net.therap.app.dto.SubmittedContentsDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Content;
import net.therap.app.model.Submission;
import net.therap.app.service.ContentReleaseService;
import net.therap.app.service.ContentService;
import net.therap.app.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 7/8/25
 */
@RestController
@RequestMapping("/submission")
public class SubmissionController {
    
    private final SubmissionService submissionService;
    private final DtoHelper dtoHelper;
    private final ContentReleaseService contentReleaseService;
    private final ContentService contentService;
    
    public SubmissionController(SubmissionService submissionService, DtoHelper dtoHelper, ContentReleaseService contentReleaseService, ContentService contentService) {
        this.submissionService = submissionService;
        this.dtoHelper = dtoHelper;
        this.contentReleaseService = contentReleaseService;
        this.contentService = contentService;
    }
    
    @GetMapping("/byTeacher/{instructorId}")
    public ResponseEntity<List<ContentCatalogueDTO>> getByTeacher(@PathVariable("instructorId") long instructorId) {
        List<Submission> submissionList = contentReleaseService.findSubmissionByInstructorId(instructorId);
        
        return ResponseEntity.ok(submissionList.stream().map(dtoHelper::toContentCatalogueDTO).toList());
    }
    
    @GetMapping("/attatchments/{submissionId}")
    public ResponseEntity<List<SubmittedContentsDTO>> getAllSubmittedFiles(@PathVariable("submissionId") long submissionId) {
        Optional<Content> submissionContentOptional = contentService.findContentByContentReleaseId(submissionId);
        
        if (submissionContentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // LP fetch
//        SubmittedContentsDTO submissions = learningProcessor.getSubmittedFiles(submissionId);
//
//        return ResponseEntity.ok(submissions);
        
        return ResponseEntity.noContent().build();
    }
    
    // get submission history from Avi
    @GetMapping("/history/{studentId}/{submissionId}")
    public ResponseEntity<List<StudentSubmissionDTO>> getAllSubmissionHistoryOfStudent(@PathVariable long studentId, @PathVariable long submissionId) {
        // verify submission id
        Optional<Content> submissionContentOptional = contentService.findContentByContentReleaseId(submissionId);
        if (submissionContentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // get from avi
//        return learningProcessorClient.getSubmissionHistoryOfStudent(studentId, submissionId);    }
        return ResponseEntity.noContent().build();
    }
}