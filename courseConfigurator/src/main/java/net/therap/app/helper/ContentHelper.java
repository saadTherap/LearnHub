package net.therap.app.helper;

import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.dto.LectureCatalogDTO;
import net.therap.app.dto.SubmissionCatalogueDTO;
import net.therap.app.mapper.LectureMapper;
import net.therap.app.mapper.SubmissionMapper;
import net.therap.app.model.*;
import net.therap.app.model.Module;
import net.therap.app.model.enums.ContentType;
import net.therap.app.model.enums.ReleaseStatus;
import net.therap.app.service.ContentService;
import net.therap.app.service.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Objects.isNull;
import static net.therap.app.util.CollectionUtil.isEmptyCollection;

/**
 * @author gazizafor
 * @since 31/7/25
 */
@Component
public class ContentHelper {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    final LectureMapper lectureMapper;
    final SubmissionMapper submissionMapper;
    private final ModuleService moduleService;
    private final ContentService contentService;
    
    public ContentHelper(LectureMapper lectureMapper, SubmissionMapper submissionMapper, ModuleService moduleService, ContentService contentService) {
        this.lectureMapper = lectureMapper;
        this.submissionMapper = submissionMapper;
        this.moduleService = moduleService;
        this.contentService = contentService;
    }
    
    public Content getContent(ContentCatalogueDTO contentCatalogueDTO) {
        Content content = new Content();
        content.setTitle(contentCatalogueDTO.getTitle());
        content.setModule(getModule(contentCatalogueDTO.getModuleId()));
        content.setImageUrl(""); // to be updated
        content.setContentReleases(new ArrayList<>());
        
        return content;
    }
    
    public ContentRelease getContentRelease(ContentCatalogueDTO contentCatalogueDTO, Content content) {
        ContentType contentType = ContentType.fromString(contentCatalogueDTO.getType());
        
        switch (contentType) {
            case QUIZ:
                Quiz quiz = new Quiz();
                quiz.setRelease(ReleaseStatus.DRAFT.getReleaseNumber());
                quiz.setQuestions(new ArrayList<>());
                quiz.setContent(content);
                
                return quiz;
            
            case LECTURE:
                LectureCatalogDTO  lectureCatalogDTO = (LectureCatalogDTO) contentCatalogueDTO;
                Lecture lecture = lectureMapper.toLecture(lectureCatalogDTO);
                lecture.setContent(content);
                lecture.setRelease(ReleaseStatus.DRAFT.getReleaseNumber());
                
                return lecture;
                
            case SUBMISSION:
                SubmissionCatalogueDTO submissionCatalogueDTO = (SubmissionCatalogueDTO) contentCatalogueDTO;
                
                Submission submission = submissionMapper.toSubmission(submissionCatalogueDTO);
                submission.setContent(content);
                submission.setRelease(ReleaseStatus.DRAFT.getReleaseNumber());
                
                return submission;
                
            case null:
            default:
                throw new IllegalArgumentException("Invalid content type");
        }
    }
    
    public Module getModule(long moduleId) {
        Optional<Module> moduleOptional = moduleService.findById(moduleId);
        
        if (moduleOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        
        return moduleOptional.get();
    }
    
    public boolean isValidForPublication(ContentRelease draft) {
        logger.info("Checking if draft is ready for publication: {}", draft.getId());
        
        if (draft.getRelease() == ReleaseStatus.DRAFT.getReleaseNumber()) {
            if (draft instanceof Quiz) {
                return isValidQuiz((Quiz) draft);
            } else {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isValidQuiz(Quiz quiz) {
        return !isEmptyCollection(quiz.getQuestions());
    }
    
    public boolean isValidForPublication(ContentRelease previousRelease, ContentRelease newRelease) {
        logger.info("Checking if content release is valid, previous releaseNum is {}", previousRelease.getRelease());
        
        if (previousRelease.getRelease() == ReleaseStatus.DRAFT.getReleaseNumber() && !(previousRelease instanceof Quiz)) {
            return true;
        }
        
        boolean b = switch (previousRelease) {
            case Lecture lecture -> !isSameLecturee(lecture, (Lecture) newRelease);
            case Submission submission -> !isSameSubmission(submission, (Submission) newRelease);
            case Quiz quiz -> !isSameQuiz(quiz, (Quiz) newRelease);
            default -> throw new IllegalArgumentException("Invalid content release");
        };
        
        logger.info("isSame Content: {}", !b);
        
        if (previousRelease.getRelease() == ReleaseStatus.DRAFT.getReleaseNumber()) {
            return true;
        }
        
        return b;
    }
    
    public boolean isSameLecturee(Lecture previousLecture, Lecture newLecture) {
        return previousLecture.getDescription().equals(newLecture.getDescription()) &&
                previousLecture.getResourceLink().equals(newLecture.getResourceLink()) &&
                previousLecture.getVideoUrl().equals(newLecture.getVideoUrl());
    }
    
    public boolean isSameSubmission(Submission previousSubmission, Submission newSubmission) {
        return previousSubmission.getDescription().equals(newSubmission.getDescription()) &&
                previousSubmission.getResourceLink().equals(newSubmission.getResourceLink());
    }
    
    public boolean isSameQuiz(Quiz previousQuiz, Quiz newQuiz) {
        contentService.loadQuestions(previousQuiz);
        
        if (isNull(newQuiz.getQuestions())) {
            newQuiz.setQuestions(new ArrayList<>());
        }
        
        if (previousQuiz.getQuestions().size() != newQuiz.getQuestions().size()) {
            return false;
        }
        
        for(int i = 0; i < previousQuiz.getQuestions().size(); i++) {
            if (!previousQuiz.getQuestions().get(i).equals(newQuiz.getQuestions().get(i))) {
                return false;
            }
        }
        
        return true;
    }
}