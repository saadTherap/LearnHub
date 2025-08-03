package net.therap.app.service;

import net.therap.app.model.Content;
import net.therap.app.model.Lecture;
import net.therap.app.repository.ContentRepository;
import net.therap.app.repository.LectureRepository;
import net.therap.app.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Service
@Transactional(readOnly = true)
public class LectureService {
    
    @Autowired
    private LectureRepository lectureRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private ContentRepository contentRepository;
    
    public List<Lecture> findAll() {
        return lectureRepository.findAll();
    }
    
    public Optional<Lecture> findById(long id) {
        return lectureRepository.findById(id);
    }
    
    @Transactional
    public Lecture save(Lecture lecture) {
        return lectureRepository.save(lecture);
    }
    
    @Transactional
    public Lecture createLecture(Lecture lecture, long contentId) {
        Optional<Content> contentOptional = contentRepository.findById(contentId);
        if (contentOptional.isPresent()) {
            lecture.setContent(contentOptional.get());
            return lectureRepository.save(lecture);
        }
        throw new RuntimeException("Content not found with ID: " + contentId);
    }
    
    @Transactional
    public Lecture updateLecture(long id, Lecture lectureDetails) {
        Optional<Lecture> lectureOptional = lectureRepository.findById(id);
        if (lectureOptional.isPresent()) {
            Lecture existingLecture = lectureOptional.get();
            existingLecture.getContent().setTitle(lectureDetails.getContent().getTitle());
            existingLecture.setDescription(lectureDetails.getDescription());
            existingLecture.setVideoUrl(lectureDetails.getVideoUrl());
            existingLecture.setResourceLink(lectureDetails.getResourceLink());
            return lectureRepository.save(existingLecture);
        }
        throw new RuntimeException("Lecture not found with ID: " + id);
    }
    
    @Transactional
    public void deleteById(long id) {
        lectureRepository.deleteById(id);
    }
}