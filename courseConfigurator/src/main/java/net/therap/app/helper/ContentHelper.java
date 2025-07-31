package net.therap.app.helper;

import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.dto.LectureCatalogDTO;
import net.therap.app.dto.QuizCatalogDTO;
import net.therap.app.dto.SubmissionCatalogueDTO;
import net.therap.app.mapper.LectureMapper;
import net.therap.app.mapper.SubmissionMapper;
import net.therap.app.model.*;
import net.therap.app.model.enums.ContentType;
import net.therap.app.model.enums.ReleaseStatus;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author gazizafor
 * @since 31/7/25
 */
@Component
public class ContentHelper {
    
    final LectureMapper lectureMapper;
    final SubmissionMapper submissionMapper;
    
    public ContentHelper(LectureMapper lectureMapper, SubmissionMapper submissionMapper) {
        this.lectureMapper = lectureMapper;
        this.submissionMapper = submissionMapper;
    }
    
    public ContentRelease getContentRelease(ContentCatalogueDTO contentCatalogueDTO) {
        ContentType contentType = ContentType.fromString(contentCatalogueDTO.getType());
        
        switch (contentType) {
            case QUIZ:
                Quiz quiz = new Quiz();
                quiz.setRelease(ReleaseStatus.DRAFT.getReleaseNumber());
                quiz.setQuestions(new ArrayList<>());
                
                return quiz;
            
            case LECTURE:
                LectureCatalogDTO  lectureCatalogDTO = (LectureCatalogDTO) contentCatalogueDTO;
                
                return lectureMapper.toLecture(lectureCatalogDTO);
                
            case SUBMISSION:
                SubmissionCatalogueDTO submissionCatalogueDTO = (SubmissionCatalogueDTO) contentCatalogueDTO;
                
                return submissionMapper.toSubmission(submissionCatalogueDTO);
                
            default:
                throw new IllegalArgumentException("Invalid content type");
        }
    }
}