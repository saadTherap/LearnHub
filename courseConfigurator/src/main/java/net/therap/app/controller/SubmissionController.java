package net.therap.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.dto.SubmittedContentsDTO;
import net.therap.app.service.AuthorizationService;
import net.therap.app.helper.DtoHelper;
import net.therap.app.model.Content;
import net.therap.app.model.Submission;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.service.ContentReleaseService;
import net.therap.app.service.ContentService;
import net.therap.app.service.SubmissionService;
import org.apache.coyote.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 7/8/25
 */
@RestController
@RequestMapping("/submission")
@Slf4j
public class SubmissionController {
    
    private final SubmissionService submissionService;
    private final DtoHelper dtoHelper;
    private final ContentReleaseService contentReleaseService;
    private final ContentService contentService;
    private final AuthorizationService authorizationService;
    private final MessageSource messageSource;
    
    public SubmissionController(SubmissionService submissionService, DtoHelper dtoHelper, ContentReleaseService contentReleaseService, ContentService contentService, AuthorizationService authorizationService, MessageSource messageSource) {
        this.submissionService = submissionService;
        this.dtoHelper = dtoHelper;
        this.contentReleaseService = contentReleaseService;
        this.contentService = contentService;
        this.authorizationService = authorizationService;
        this.messageSource = messageSource;
    }
    
    @GetMapping("/byTeacher")
    public ResponseEntity<List<ContentCatalogueDTO>> getByTeacher(HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /submissions/byTeacher");
        long instructorId = authorizationService.getInstructorIdFromRequest(request);
        List<Submission> submissionList = contentReleaseService.findSubmissionByInstructorId(instructorId);
        
        return ResponseEntity.ok(submissionList.stream().map(dtoHelper::toContentCatalogueDTO).toList());
    }
    
    @GetMapping("/attachments/{submissionId}")
    public ResponseEntity<List<SubmittedContentsDTO>> getAllSubmittedFiles(@PathVariable("submissionId") long submissionId,
                                                                           HttpServletRequest request) throws BadRequestException {
        
        log.info("[GET] /submission/attachments/{}", submissionId);
        Optional<Content> submissionContentOptional = contentService.findContentByContentReleaseId(submissionId);
        
        if (submissionContentOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("not.found.submission", null, Locale.getDefault()));
        }
        
        authorizationService.authorize(AuthorizationLevel.OWNER, submissionContentOptional.get(), request);
        // LP fetch
//        SubmittedContentsDTO submissions = learningProcessor.getSubmittedFiles(submissionId);
//
//        return ResponseEntity.ok(submissions);
        
        return ResponseEntity.noContent().build();
    }
}