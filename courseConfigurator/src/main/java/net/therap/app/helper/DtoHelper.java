package net.therap.app.helper;

import net.therap.app.dto.*;
import net.therap.app.model.*; // Import all your entity classes
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import net.therap.app.model.Module;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * @author gazizafor
 * @since 22/7/25
 */
@Component
public class DtoHelper {
    
    Logger logger = LoggerFactory.getLogger(this.getClass());
    
    // --- Instructor Mapping ---
    public InstructorDTO toInstructorDTO(Instructor instructor) {
        if (instructor == null) {
            return null;
        }
        InstructorDTO dto = new InstructorDTO();
        BeanUtils.copyProperties(instructor, dto); // Exclude 'courses' to avoid direct recursion
        
//        if (instructor.getCourses() != null && !instructor.getCourses().isEmpty()) {
//            dto.setCourses(instructor.getCourses().stream()
//                                   .map(this::toCourseDTOWithoutInstructor) // Use a specialized mapping method
//                                   .collect(Collectors.toList()));
//        }
        return dto;
    }
    
    // --- Course Mapping (Full Details) ---
    public CourseDTO toCourseDTO(Course course) {
        if (course == null) {
            return null;
        }
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(course, dto, "instructor", "modules"); // Exclude instructor and modules for controlled mapping
        
        // Set instructor details (only ID and name to prevent recursion)
        Optional.ofNullable(course.getInstructor()).ifPresent(instructor -> {
            dto.setInstructorId(instructor.getId());
            dto.setInstructorName(instructor.getName());
        });
        
        // Map modules to ModuleDTOs
        if (course.getModules() != null && !course.getModules().isEmpty()) {
            dto.setModules(course.getModules().stream()
                                   .map(this::toModuleDTO)
                                   .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    // --- Course Catalog Mapping (Public) ---
    public CourseCatalogDTO toCourseCatalogDTO(Course course) {
        if (course == null) {
            return null;
        }
        
        CourseCatalogDTO dto = new CourseCatalogDTO();
        dto.setCourseId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setCurrentPublishedVersion(course.getCurrentRelease());
        
        Optional.ofNullable(course.getInstructor()).ifPresent(instructor -> {
            dto.setInstructorName(instructor.getName());
        });
        
        // REMOVED: dto.setModules(new ArrayList<>()); and the loop
        // CourseCatalogDTO does not have a List<String> modules field.
        // It only contains high-level course metadata.
        
        return dto;
    }
    
    public CourseCatalogDTO toDetailedCourseCatalogDTO(Course course) {
        if (course == null) {
            return null;
        }
        
        CourseCatalogDTO dto = new CourseCatalogDTO();
        dto.setCourseId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setCurrentPublishedVersion(course.getCurrentRelease());
        
        Optional.ofNullable(course.getInstructor()).ifPresent(instructor -> {
            dto.setInstructorName(instructor.getName());
        });
        
        dto.setModules(course.getModules().stream().map(this::toModuleCatalogueDTO).collect(Collectors.toList()));
        
        return dto;
    }
    
    // Specialized CourseDTO mapping to avoid instructor recursion when called from InstructorDTO
    private CourseDTO toCourseDTOWithoutInstructor(Course course) {
        if (course == null) {
            return null;
        }
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(course, dto, "instructor", "modules");
        
        Optional.ofNullable(course.getInstructor()).ifPresent(instructor -> {
            dto.setInstructorId(instructor.getId());
            dto.setInstructorName(instructor.getName());
        });
        
        return dto;
    }
    
    public ModuleCatalogDTO toModuleCatalogueDTO(Module module) {
        ModuleCatalogDTO dto = getModuleCatalogueDTO(module);
        if (isNull(dto)) {
            return null;
        }
        
        if (module.getContents() != null && !module.getContents().isEmpty()) {
            dto.setContents(module.getContents().stream()
                                    .map(this::toContentCatalogueDTO)
                                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public ModuleDTO toModuleDTO(Module module) {
        ModuleDTO dto = getModuleDTO(module);
        if (isNull(dto)) {
            return null;
        }
        
        if (module.getContents() != null && !module.getContents().isEmpty()) {
            dto.setContents(module.getContents().stream()
                                    .map(this::toDetailedContentDTO)
                                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public ModuleDTO toModuleDtoLazy(Module module) {
        ModuleDTO dto = getModuleDTO(module);
        
        if (isNull(dto)) {
            return null;
        }
        
        module.setContents(null);
        
        return dto;
    }
    
    // --- Content Mapping (Logical Lesson) ---
    public ContentDTO toDetailedContentDTO(Content content) {
        if (content == null) {
            return null;
        }
        ContentDTO dto = new ContentDTO(); // This line is now valid
        BeanUtils.copyProperties(content, dto, "module", "currentContentRelease", "contentReleases");
        
        // Set module ID
        Optional.ofNullable(content.getModule()).ifPresent(module -> dto.setModuleId(module.getId()));
        
        // Map currentContentRelease to its DTO
        Optional.ofNullable(content.getCurrentContentRelease()).ifPresent(currentRelease -> {
            dto.setCurrentContentRelease(this.toContentReleaseDTO(currentRelease)); // Map to ContentReleaseDTO
        });
        
        // Map all content releases (versions)
        
        logger.info("Mapping content releases: {}", content.getContentReleases().size());
        if (content.getContentReleases() != null && !content.getContentReleases().isEmpty()) {
            dto.setContentReleases(content.getContentReleases().stream()
                                           .map(this::toContentReleaseDTO) // Map to ContentReleaseDTO
                                           .collect(Collectors.toList()));
        }
//        dto.setContentReleases(null);
        
        return dto;
    }
    
    public ContentReleaseDTO toContentReleaseDTO(ContentRelease contentRelease) {
        if (contentRelease == null) {
            return null;
        }
        
        ContentReleaseDTO dto;
        
        long id = contentRelease.getId();
        long orderedIndex = contentRelease.getOrderIndex();
        long releaseNum = contentRelease.getRelease();
        long contentId = contentRelease.getContent() != null ? contentRelease.getContent().getId() : null;
        String title = contentRelease.getContent() != null ? contentRelease.getContent().getTitle() : null; // Title comes from logical content
        
        if (contentRelease instanceof Lecture lecture) {
            dto = new LectureDTO(
                    id, releaseNum, orderedIndex, title, contentId,
                    lecture.getDescription(), lecture.getVideoUrl(), lecture.getResourceLink()
            );
            
        } else if (contentRelease instanceof Quiz quiz) {
            dto = new QuizDTO(
                    id, releaseNum, orderedIndex, title, contentId,
                    Optional.ofNullable(quiz.getQuestions())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(this::toQuizQuestionDTO)
                            .collect(Collectors.toList())
            );
            
        } else if (contentRelease instanceof Submission submission) {
            dto = new SubmissionDTO(
                    id, releaseNum, orderedIndex, title, contentId,
                    submission.getDescription(), submission.getResourceLink()
            );
            
        } else {
            dto = null;
        }
        
        return dto;
    }
    
    public QuizQuestionDTO toQuizQuestionDTO(QuizQuestion question) {
        if (question == null) {
            return null;
        }
        QuizQuestionDTO dto = new QuizQuestionDTO();
        BeanUtils.copyProperties(question, dto, "quizRelease", "options");
        
        Optional.ofNullable(question.getQuiz()).ifPresent(quizRelease -> {
            dto.setQuizId(quizRelease.getId());
        });
        
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            dto.setOptions(question.getOptions().stream()
                                   .map(this::toQuizOptionDTO)
                                   .collect(Collectors.toList()));
        }
        return dto;
    }
    
    public QuizOptionDTO toQuizOptionDTO(QuizOption option) {
        if (option == null) {
            return null;
        }
        QuizOptionDTO dto = new QuizOptionDTO();
        BeanUtils.copyProperties(option, dto, "quizQuestion");
        
        Optional.ofNullable(option.getQuizQuestion()).ifPresent(quizQuestion -> dto.setQuizQuestionId(quizQuestion.getId()));
        
        return dto;
    }
    
    public ContentDTO toContentDTO(ContentRelease contentRelease) {
        if (isNull(contentRelease)) {
            return null;
        }
        
        ContentDTO dto = new ContentDTO();
        
        dto.setId(contentRelease.getId());
        dto.setTitle(contentRelease.getContent().getTitle());
//        dto.setOrderIndex(contentRelease.getOrderIndex());
        dto.setModuleId(contentRelease.getContent().getModule().getId());
        
//        if (contentRelease instanceof Lecture) {
//            dto.setType("LECTURE");
//
//        } else if (contentRelease instanceof Quiz) {
//            dto.setType("QUIZ");
//
//        } else if (contentRelease instanceof Submission) {
//            dto.setType("SUBMISSION");
//
//        } else {
//            dto.setType(null); // Fallback
//        }
        
        return dto;
    }
    
    public ContentCatalogueDTO toContentCatalogueDTO(Content content) {
        return toContentCatalogueDTO(content.getCurrentContentRelease());
    }
    
    public ContentCatalogueDTO toContentCatalogueDTO(ContentRelease contentRelease) {
        if (isNull(contentRelease)) {
            return null;
        }
        
        ContentCatalogueDTO dto = new ContentCatalogueDTO();
        
        dto.setId(contentRelease.getId());
        dto.setTitle(contentRelease.getContent().getTitle());
        dto.setOrderIndex(contentRelease.getOrderIndex());
        dto.setModuleId(contentRelease.getContent().getModule().getId());
        
        if (contentRelease instanceof Lecture) {
            dto.setType("LECTURE");
            
        } else if (contentRelease instanceof Quiz) {
            dto.setType("QUIZ");
            
        } else if (contentRelease instanceof Submission) {
            dto.setType("SUBMISSION");
            
        } else {
            dto.setType(null);
        }
        
        return dto;
    }
    
    
    
    public LectureCatalogDTO populateLectureCatalogDTO(ContentCatalogueDTO contentCatalogueDTO, ContentRelease contentRelease) {
        LectureCatalogDTO lectureCatalogDTO = new LectureCatalogDTO();
        BeanUtils.copyProperties(contentCatalogueDTO, lectureCatalogDTO);
        
        Lecture lecture = (Lecture) contentRelease;
        
        lectureCatalogDTO.setDescription(lecture.getDescription());
        lectureCatalogDTO.setResourceLink(lecture.getResourceLink());
        lectureCatalogDTO.setVideoUrl(lecture.getVideoUrl());
        
        return lectureCatalogDTO;
    }
    
    public SubmissionCatalogueDTO populateSubmissionCatalogDTO(ContentCatalogueDTO contentCatalogueDTO, ContentRelease contentRelease) {
        SubmissionCatalogueDTO submissionCatalogueDTO = new SubmissionCatalogueDTO();
        BeanUtils.copyProperties(contentCatalogueDTO, submissionCatalogueDTO);
        
        Submission submission = (Submission) contentRelease;
        
        submissionCatalogueDTO.setDescription(submission.getDescription());
        submissionCatalogueDTO.setResourceLink(submission.getResourceLink());
        
        return submissionCatalogueDTO;
    }
    
    public QuizCatalogDTO populateQuizCatalogDTO(ContentCatalogueDTO contentCatalogueDTO, ContentRelease contentRelease) {
        QuizCatalogDTO quizCatalogDTO = new QuizCatalogDTO();
        BeanUtils.copyProperties(contentCatalogueDTO, quizCatalogDTO);
        
        Quiz quiz = (Quiz) contentRelease;
        
        quizCatalogDTO.setQuestions(quiz.getQuestions()
                                            .stream()
                                            .map(this::toQuizQuestionDTO)
                                            .collect(Collectors.toList()));
        
        
        return quizCatalogDTO;
    }
    
    private static ModuleDTO getModuleDTO(Module module) {
        if (module == null) {
            return null;
        }
        
        ModuleDTO dto = new ModuleDTO();
        BeanUtils.copyProperties(module, dto, "course", "contents");
        
        Optional.ofNullable(module.getCourse()).ifPresent(course -> dto.setCourseId(course.getId()));
        return dto;
    }
    
    private static ModuleCatalogDTO getModuleCatalogueDTO(Module module) {
        if (module == null) {
            return null;
        }
        
        ModuleCatalogDTO dto = new ModuleCatalogDTO();
        BeanUtils.copyProperties(module, dto, "course", "contents");
        
        Optional.ofNullable(module.getCourse()).ifPresent(course -> dto.setCourseId(course.getId()));
        return dto;
    }
}