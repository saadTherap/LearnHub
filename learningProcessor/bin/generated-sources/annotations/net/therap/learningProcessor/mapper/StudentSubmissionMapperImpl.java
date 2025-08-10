package net.therap.learningProcessor.mapper;

import javax.annotation.processing.Generated;
import net.therap.learningProcessor.dto.submission.StudentSubmissionDto;
import net.therap.learningProcessor.entity.Student;
import net.therap.learningProcessor.entity.StudentSubmission;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-10T11:33:50+0600",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class StudentSubmissionMapperImpl implements StudentSubmissionMapper {

    @Override
    public StudentSubmissionDto toDto(StudentSubmission submission) {
        if ( submission == null ) {
            return null;
        }

        StudentSubmissionDto.StudentSubmissionDtoBuilder studentSubmissionDto = StudentSubmissionDto.builder();

        studentSubmissionDto.studentId( submissionStudentId( submission ) );
        studentSubmissionDto.contentId( submission.getContentId() );
        studentSubmissionDto.downloadUrl( submission.getDownloadUrl() );
        studentSubmissionDto.id( submission.getId() );
        studentSubmissionDto.originalFileName( submission.getOriginalFileName() );
        studentSubmissionDto.submittedAt( submission.getSubmittedAt() );

        studentSubmissionDto.studentName( submission.getStudent().getFirstName() + " " + submission.getStudent().getLastName() );

        return studentSubmissionDto.build();
    }

    @Override
    public StudentSubmission toEntity(StudentSubmissionDto dto) {
        if ( dto == null ) {
            return null;
        }

        StudentSubmission studentSubmission = new StudentSubmission();

        studentSubmission.setStudent( studentSubmissionDtoToStudent( dto ) );
        if ( dto.getContentId() != null ) {
            studentSubmission.setContentId( dto.getContentId() );
        }
        studentSubmission.setDownloadUrl( dto.getDownloadUrl() );
        if ( dto.getId() != null ) {
            studentSubmission.setId( dto.getId() );
        }
        studentSubmission.setOriginalFileName( dto.getOriginalFileName() );
        studentSubmission.setSubmittedAt( dto.getSubmittedAt() );

        return studentSubmission;
    }

    private Long submissionStudentId(StudentSubmission studentSubmission) {
        if ( studentSubmission == null ) {
            return null;
        }
        Student student = studentSubmission.getStudent();
        if ( student == null ) {
            return null;
        }
        long id = student.getId();
        return id;
    }

    protected Student studentSubmissionDtoToStudent(StudentSubmissionDto studentSubmissionDto) {
        if ( studentSubmissionDto == null ) {
            return null;
        }

        Student student = new Student();

        if ( studentSubmissionDto.getStudentId() != null ) {
            student.setId( studentSubmissionDto.getStudentId() );
        }

        return student;
    }
}
