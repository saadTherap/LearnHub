package net.therap.app.controller;//package net.therap.app.controller;
//
//import net.therap.app.dto.ContentDTO;
//import net.therap.app.dto.LectureDTO;
//import net.therap.app.dto.QuizDTO;
//import net.therap.app.dto.SubmissionDTO;
//import net.therap.app.model.*;
//import net.therap.app.model.ContentRelease; // Base ContentRelease entity
//import net.therap.app.model.Module;
//import net.therap.app.service.*;
//import net.therap.app.helper.DtoHelper; // Import DtoHelper
//import org.springframework.beans.BeanUtils; // Keep if still used for DTO to Entity mapping
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * @author gazizafor
// * @since 22/7/25
// */
//@RestController
//@RequestMapping("/api/lessons")
//public class LessonController {
//
//    private final LessonService lessonService;
//    private final ModuleService moduleService; // Needed for DTO to Entity mapping
//    private final DtoHelper dtoHelper; // Inject DtoHelper
//    private final LectureService lectureService;
//    private final QuizService quizService;
//    private final SubmissionService submissionService;
//
//    // Use constructor injection for all dependencies
//    public LessonController(LessonService lessonService,
//                            ModuleService moduleService,
//                            DtoHelper dtoHelper, LectureService lectureService, QuizService quizService, SubmissionService submissionService) {
//        this.lessonService = lessonService;
//        this.moduleService = moduleService;
//        this.dtoHelper = dtoHelper;
//        this.lectureService = lectureService;
//        this.quizService = quizService;
//        this.submissionService = submissionService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<ContentDTO>> getAllLessons() {
//        List<ContentRelease> contentReleases = lessonService.findAllContentReleases(); // Assuming findAllContentReleases returns all BaseLesson types
//        List<ContentDTO> lessonDTOs = contentReleases.stream()
//                .map(dtoHelper::toContentReleaseDTO)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(lessonDTOs);
//    }
//
//    @GetMapping("/{id}/{release}")
//    public ResponseEntity<ContentDTO> getLessonById(@PathVariable Long id, @PathVariable Long release) {
//        Optional<ContentRelease> lessonOptional = lessonService.findById(id);
//        return lessonOptional
//                .map(dtoHelper::toLessonDTO)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @PostMapping("/lecture")
//    public ResponseEntity<?> createLecture(@RequestBody LectureDTO lectureDTO) {
//        // --- DTO to Entity Mapping for Creation ---
//        // This mapping needs to be careful with composite PKs and relationships.
//        Lecture lecture = new Lecture();
//        // BeanUtils.copyProperties(lectureDTO, lecture); // BeanUtils might not handle complex types/relationships well
//
//        // Manually map fields from DTO to entity for creation
//        // The id 'id' and 'release' will be assigned by service logic for new content versions
//        lecture.getContent().setTitle(lectureDTO.getTitle());
//        lecture.setDescription(lectureDTO.getDescription());
//        lecture.setVideoUrl(lectureDTO.getVideoUrl());
//        lecture.setResourceLink(lectureDTO.getResourceLink());
//
//        // Fetch and set the Module entity
//        Module module = moduleService.findById(lectureDTO.getModuleId())
//                .orElseThrow(() -> new NoSuchElementException("Module not found with ID: " + lectureDTO.getModuleId()));
//        lecture.setModule(module);
//
//        try {
//            // Assuming createLecture in service handles id assignment and saving
//            Lecture savedLecture = (Lecture) lectureService.createLecture(lecture, lectureDTO.getModuleId()); // Assuming service takes type
//            return new ResponseEntity<>(dtoHelper.toLessonDTO(savedLecture), HttpStatus.CREATED); // <<< CHANGED: Use DtoHelper
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PostMapping("/quiz")
//    public ResponseEntity<?> createQuiz(@RequestBody QuizDTO quizDTO) {
//        // --- DTO to Entity Mapping for Creation ---
//        Quiz quiz = new Quiz();
//        quiz.getContent().setTitle(quizDTO.getTitle());
//        // Map questions from QuizQuestionDTO to QuizQuestion entities
//        // This requires a DTO to Entity mapper for QuizQuestion and QuizOption as well
//        // For simplicity, this part is omitted, but would be complex.
//
//        Module module = moduleService.findById(quizDTO.getModuleId())
//                .orElseThrow(() -> new NoSuchElementException("Module not found with ID: " + quizDTO.getModuleId()));
//        quiz.getContent().setModule(module);
//
//        try {
//            Quiz savedQuiz = (Quiz) quizService.createQuiz(quiz, quizDTO.getModuleId()); // Assuming service takes type
//            return new ResponseEntity<>(dtoHelper.toLessonDTO(savedQuiz), HttpStatus.CREATED); // <<< CHANGED: Use DtoHelper
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PostMapping("/submission")
//    public ResponseEntity<?> createSubmission(@RequestBody SubmissionDTO submissionDTO) {
//        // --- DTO to Entity Mapping for Creation ---
//        Submission submission = new Submission();
//        submission.getContent().setTitle(submissionDTO.getTitle());
//        submission.setDescription(submissionDTO.getDescription());
//        submission.setResourceLink(submissionDTO.getResourceLink());
//
//        Module module = moduleService.findById(submissionDTO.getModuleId())
//                .orElseThrow(() -> new NoSuchElementException("Module not found with ID: " + submissionDTO.getModuleId()));
//        submission.getContent().setModule(module);
//
//        try {
//            Submission savedSubmission = (Submission) submissionService.createSubmission(submission, submission.getContent().getModule().getId()); // Assuming service takes type
//            return new ResponseEntity<>(dtoHelper.toLessonDTO(savedSubmission), HttpStatus.CREATED); // <<< CHANGED: Use DtoHelper
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PutMapping("/lecture/{id}/{release}")
//    public ResponseEntity<?> updateLecture(@PathVariable Long id, @PathVariable Long release, @RequestBody LectureDTO lectureDTO) {
//        // --- DTO to Entity Mapping for Update ---
//        // Fetch existing entity and update its fields
//        Lecture lectureDetails = new Lecture(); // This should ideally be the fetched entity
//        BeanUtils.copyProperties(lectureDTO, lectureDetails); // This will only copy simple fields
//
//        try {
//            // Assuming updateLecture in service handles finding existing version and creating new if content changes
//            Lecture updatedLecture = (Lecture) lectureService.updateLecture(id, lectureDetails); // Assuming service takes type
//            return ResponseEntity.ok(dtoHelper.toLessonDTO(updatedLecture)); // <<< CHANGED: Use DtoHelper
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @PutMapping("/quiz/{id}/{release}")
//    public ResponseEntity<?> updateQuiz(@PathVariable Long id, @PathVariable Long release, @RequestBody QuizDTO quizDTO) {
//        // --- DTO to Entity Mapping for Update ---
//        Quiz quizDetails = new Quiz(); // This should ideally be the fetched entity
//        BeanUtils.copyProperties(quizDTO, quizDetails); // This will only copy simple fields
//
//        try {
//            Quiz updatedQuiz = (Quiz) quizService.updateQuiz(id, quizDetails); // Assuming service takes type
//            return ResponseEntity.ok(dtoHelper.toLessonDTO(updatedQuiz)); // <<< CHANGED: Use DtoHelper
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @PutMapping("/submission/{id}/{release}")
//    public ResponseEntity<?> updateSubmission(@PathVariable Long id, @PathVariable Long release, @RequestBody SubmissionDTO submissionDTO) {
//        // --- DTO to Entity Mapping for Update ---
//        Submission submissionDetails = new Submission(); // This should ideally be the fetched entity
//        BeanUtils.copyProperties(submissionDTO, submissionDetails); // This will only copy simple fields
//
//        try {
//            Submission updatedSubmission = (Submission) submissionService.updateSubmission(id, submissionDetails); // Assuming service takes type
//            return ResponseEntity.ok(dtoHelper.toLessonDTO(updatedSubmission)); // <<< CHANGED: Use DtoHelper
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/{id}/{release}")
//    public ResponseEntity<Void> deleteLesson(@PathVariable Long id, @PathVariable Long release) {
//        // Assuming deleteById in service handles soft delete logic
//        if (lessonService.findById(id).isPresent()) {
//            lessonService.deleteById(id); // Assuming this is soft delete in service
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.notFound().build();
//    }
//}