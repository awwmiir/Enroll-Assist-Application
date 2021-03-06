package ir.proprog.enrollassist.repository;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListView;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class StudentRepositoryTest {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EnrollmentListRepository enrollmentRepository;
    @Autowired
    private MajorRepository majorRepository;



    @Test
    public void Student_with_specific_student_number_is_returned_correctly() throws ExceptionList {
        Major major = new Major("12", "CE", "Engineering");
        majorRepository.save(major);
        Student mahsa = new Student("810199999", "Undergraduate");
        studentRepository.save(mahsa);
        Optional<Student> res = studentRepository.findByStudentNumber(new StudentNumber("810199999"));
        assertThat(res.get().getStudentNumber().getNumber()).isEqualTo("810199999");
    }

    @Test
    public void No_student_is_returned_if_student_number_is_invalid() {
        Optional<Student> res = studentRepository.findByStudentNumber(new StudentNumber("810199999"));
        assertThat(res).isEmpty();
    }

    @Test
    public void All_lists_for_student_with_specific_student_number_is_returned_correctly() throws ExceptionList {
        Major major = new Major("12", "CE", "Engineering");
        majorRepository.save(major);
        Student john = new Student("810100000", "Undergraduate");
        studentRepository.save(john);
        EnrollmentList list1 = new EnrollmentList("1st list", john);
        EnrollmentList list2 = new EnrollmentList("2nd list", john);
        enrollmentRepository.save(list1);
        enrollmentRepository.save(list2);
        List<EnrollmentListView> res = studentRepository.findAllListsForStudent("810100000");
        assertThat(res).hasSize(2);
    }
}
