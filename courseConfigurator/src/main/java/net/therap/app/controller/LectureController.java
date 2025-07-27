package net.therap.app.controller;

import net.therap.app.dto.LectureDTO;
import net.therap.app.model.Lecture;
import net.therap.app.service.LectureService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@RestController
@RequestMapping("/api/lectures")
public class LectureController {
    
    @Autowired
    private LectureService lectureService;
    
    @GetMapping
    public ResponseEntity<List<LectureDTO>> getAllLectures() {
        List<Lecture> lectures = lectureService.findAll();
        List<LectureDTO> LectureDTOs = lectures.stream()
                .map(lecture -> {
                    LectureDTO dto = new LectureDTO();
                    BeanUtils.copyProperties(lecture, dto);
                    dto.setId(lecture.getId());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(LectureDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LectureDTO> getLectureById(@PathVariable Long id) {
//        Optional<Lecture> lectureOptional = lectureService.findById(pk);
//        return lectureOptional.map(lecture -> {
//            LectureDTO dto = new LectureDTO();
//            BeanUtils.copyProperties(lecture, dto);
//            if (lecture.getPk()!= null) {
//                dto.setLessonId(lecture.getPk());
//            }
//            return ResponseEntity.ok(dto);
//        }).orElseGet(() -> ResponseEntity.notFound().build());
        return ResponseEntity.ok(new LectureDTO());
    }
    
    @PostMapping
    public ResponseEntity<LectureDTO> createLecture(@RequestBody LectureDTO LectureDTO) {
        Lecture lecture = new Lecture();
        BeanUtils.copyProperties(LectureDTO, lecture);
        try {
            Lecture savedLecture = lectureService.createLecture(lecture, LectureDTO.getContentId());
            LectureDTO responseDto = new LectureDTO();
            BeanUtils.copyProperties(savedLecture, responseDto);
            responseDto.setId(savedLecture.getId());
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LectureDTO> updateLecture(@PathVariable Long id, @RequestBody LectureDTO LectureDTO) {
//        Lecture lectureDetails = new Lecture();
//        BeanUtils.copyProperties(LectureDTO, lectureDetails);
//        try {
//            Lecture updatedLecture = lectureService.updateLecture(pk, lectureDetails);
//            LectureDTO responseDto = new LectureDTO();
//            BeanUtils.copyProperties(updatedLecture, responseDto);
//            responseDto.setLessonId(updatedLecture.getPk());
//            return ResponseEntity.ok(responseDto);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
        return ResponseEntity.ok(new LectureDTO());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long id) {
//        if (lectureService.findById(pk).isPresent()) {
//            lectureService.deleteById(pk);
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }
}
