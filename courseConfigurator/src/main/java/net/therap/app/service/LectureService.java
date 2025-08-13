package net.therap.app.service;

import net.therap.app.model.Content;
import net.therap.app.model.Lecture;
import net.therap.app.repository.ContentRepository;
import net.therap.app.repository.LectureRepository;
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
@Service
@Transactional(readOnly = true)
public class LectureService {
    
    private final LectureRepository lectureRepository;
    
    private final ContentRepository contentRepository;
    private final MessageSource messageSource;
    
    public LectureService(LectureRepository lectureRepository, ContentRepository contentRepository, MessageSource messageSource) {
        this.lectureRepository = lectureRepository;
        this.contentRepository = contentRepository;
        this.messageSource = messageSource;
    }
    
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
    public Lecture deleteById(long id) {
        Optional<Lecture> lectureOptional = lectureRepository.findById(id);
        
        if (lectureOptional.isPresent()) {
            lectureOptional.get().setDeleted(true);
            return lectureRepository.save(lectureOptional.get());
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.lecture", null, Locale.getDefault()));
    }
}