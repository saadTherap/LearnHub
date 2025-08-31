//package net.therap.learningProcessor.repository;
//
//import net.therap.learningProcessor.entity.Student;
//import net.therap.learningProcessor.eum.Gender;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
///**
// * @author avidewan
// * @since 8/28/25
// */
//@DataJpaTest
//@ActiveProfiles("test")
//@ContextConfiguration(classes = {StudentRepository.class})
//class StudentRepositoryTest {
//
//    @Autowired
//    private StudentRepository studentRepository;
//
//    private Student createStudent(String email, String firstName, String lastName, Gender gender) {
//        Student student = new Student();
//
//        student.setEmail(email);
//        student.setFirstName(firstName);
//        student.setLastName(lastName);
//        student.setGender(gender);
//        student.setDateOfBirth(LocalDate.of(2000, 1, 1));
//
//        return student;
//    }
//
//    @Test
//    void shouldSaveAndFindStudentByEmail() {
//        Student student = createStudent("john.doe@example.com", "John", "Doe", Gender.MALE);
//        studentRepository.save(student);
//
//        Optional<Student> found = studentRepository.findByEmail("john.doe@example.com");
//
//        assertThat(found).isPresent();
//        assertThat(found.get().getFirstName()).isEqualTo("John");
//    }
//
//    @Test
//    void shouldReturnEmptyIfEmailNotFound() {
//        Optional<Student> found = studentRepository.findByEmail("nonexistent@example.com");
//
//        assertThat(found).isEmpty();
//    }
//
//    @Test
//    void shouldFindAllStudents() {
//        Student s1 = createStudent("a@example.com", "A", "A", Gender.FEMALE);
//        Student s2 = createStudent("b@example.com", "B", "B", Gender.MALE);
//
//        studentRepository.saveAll(List.of(s1, s2));
//
//        List<Student> students = studentRepository.findAll();
//
//        assertThat(students).hasSize(2);
//    }
//
//    @Test
//    void shouldDeleteStudent() {
//        Student student = createStudent("delete.me@example.com", "Delete", "Me", Gender.MALE);
//        studentRepository.save(student);
//        Long id = student.getId();
//
//        studentRepository.deleteById(id);
//        Optional<Student> deleted = studentRepository.findById(id);
//
//        assertThat(deleted).isEmpty();
//    }
//
//    @Test
//    void shouldEnforceUniqueEmailConstraint() {
//        Student s1 = createStudent("unique@example.com", "First", "One", Gender.FEMALE);
//        studentRepository.save(s1);
//
//        Student s2 = createStudent("unique@example.com", "Second", "Two", Gender.MALE);
//
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            studentRepository.saveAndFlush(s2);
//        });
//    }
//
//    @Test
//    void shouldPersistGenderEnumCorrectly() {
//        Student student = createStudent("gender@example.com", "Gender", "Test", Gender.MALE);
//        studentRepository.save(student);
//
//        Optional<Student> found = studentRepository.findByEmail("gender@example.com");
//
//        assertThat(found).isPresent();
//        assertThat(found.get().getGender()).isEqualTo(Gender.MALE);
//    }
//}
//
