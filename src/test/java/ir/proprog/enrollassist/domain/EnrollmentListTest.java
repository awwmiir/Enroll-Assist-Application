package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnrollmentListTest {
    @Test
    void Enrollment_list_returns_no_violation_when_all_courses_prerequisites_have_been_passed() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("3", "PROG", 4);
        Course ap = new Course("3", "AP", 3).withPre(prog);
        Course math2 = new Course("5", "MATH2", 3).withPre(math1);
        Section math2_1 = new Section(math2, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(prog)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, ap_1);
        assertThat(list.checkHasPassedAllPrerequisites(bebe))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Enrollment_list_cannot_have_courses_already_been_passed_by_owner() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Section math1_1 = new Section(math1, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1);
        assertThat(list.checkHasNotAlreadyPassedCourses(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Section math1_1 = new Section(math1, "01");
        Section math1_2 = new Section(math1, "02");
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, math1_2);
        assertThat(list.checkNoCourseHasRequestedTwice())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_14_credits_belonging_to_student_with_gpa_less_than_12() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 3);
        Course ap = new Course("4", "AP", 3);
        Course ds = new Course("6", "DS", 3);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        Section ds_1 = new Section(ds, "01");
        when(bebe.calculateGPA()).thenReturn(11.5F);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, ap_1, ds_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_more_than_20_credits_belonging_to_student_with_gpa_less_than_17() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 4);
        Course eco = new Course("3", "ECO", 6);
        Course ap = new Course("4", "AP", 9);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        Section ap_1 = new Section(ap, "01");
        when(bebe.calculateGPA()).thenReturn(16F);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1, ap_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Nobody_can_take_more_than_24_credits() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 6);
        Course prog = new Course("2", "PROG", 8);
        Course eco = new Course("3", "ECO", 12);
        Section math1_1 = new Section(math1, "01");
        Section prog_1 = new Section(prog, "01");
        Section eco_1 = new Section(eco, "01");
        when(bebe.calculateGPA()).thenReturn(20F);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math1_1, prog_1, eco_1);
        assertThat(list.checkValidGPALimit(bebe))
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Enrollment_list_cannot_have_duplicate_courses_and_more_than_24_credits() {
        Student bebe = mock(Student.class);
        Course math1 = new Course("1", "MATH1", 3);
        Course prog = new Course("2", "PROG", 6);
        Course math2 = new Course("5", "MATH2", 8).withPre(math1);
        Course da = new Course("7", "DS", 8);
        Course dm = new Course("8", "DM", 3);
        Section math2_1 = new Section(math2, "01");
        Section prog_1 = new Section(prog, "01");
        Section da_1 = new Section(da, "01");
        Section dm_1 = new Section(dm, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.calculateGPA()).thenReturn(20F);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        EnrollmentList list = new EnrollmentList("bebe's list", bebe);
        list.addSections(math2_1, prog_1, da_1, dm_1, dm_1);
        ArrayList<EnrollmentRuleViolation> violations = (ArrayList<EnrollmentRuleViolation>) list.checkEnrollmentRules();
        assertThat(list.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
        assertThat(violations.get(0).getClass())
                .isEqualTo(CourseRequestedTwice.class);
        assertThat(violations.get(1).getClass())
                .isEqualTo(MaxCreditsLimitExceeded.class);
    }

    @Test
    void New_students_cant_take_more_than_twenty_credits(){
        Student bebe = mock(Student.class);
        Course phys1 = new Course("8", "PHYS1", 6);
        Course prog = new Course("7", "PROG", 8);
        Course maaref = new Course("5", "MAAREF", 4);
        Course dm = new Course("3", "DM", 6);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section dm_1 = new Section(dm, "01");
        Section maaref1_1 = new Section(maaref, "01");
        when(bebe.calculateGPA()).thenReturn(0.0F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, dm_1, maaref1_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void Students_With_GPA_More_Than_Twelve_Can_Take_More_Than_Fourteen_Credits(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 6);
        Course phys1 = new Course("8", "PHYS1", 6);
        Course prog = new Course("7", "PROG", 7);
        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section math1_1 = new Section(math1, "01");
        when(bebe.calculateGPA()).thenReturn(12.0F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, math1_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Students_With_GPA_More_Than_Seventeen_Can_Take_More_Than_Twenty_Credits(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 6);
        Course phys1 = new Course("8", "PHYS1", 6);
        Course prog = new Course("7", "PROG", 8);
        Course maaref = new Course("5", "MAAREF", 4);

        Section phys1_1 = new Section(phys1, "01");
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.getTotalTakenCredits()).thenReturn(1);
        when(bebe.calculateGPA()).thenReturn(17.01F);
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys1_1, prog1_1, maaref1_1);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void Requesting_Courses_With_Two_Prerequisites_Which_None_Has_Been_Passed_Violates_Two_Rules(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course prog = new Course("7", "PROG", 4);
        Course maaref = new Course("5", "MAAREF", 2);
        Course economy = new Course("1", "ECO", 3);
        Course akhlagh = new Course("11", "AKHLAGH", 2);
        Course phys2 = new Course("9", "PHYS2", 3).withPre(math1, phys1);
        Section prog1_1 = new Section(prog, "01");
        Section maaref1_1 = new Section(maaref, "01");
        Section economy1_1 = new Section(economy, "01");
        Section akhlagh_1 = new Section(akhlagh, "01");
        Section phys2_1 = new Section(phys2, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(prog1_1, maaref1_1, economy1_1, akhlagh_1, phys2_1);
        when(bebe.calculateGPA()).thenReturn(15F);
        when(bebe.hasPassed(math1)).thenReturn(false);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void Requesting_Courses_With_2_Prerequisites_Which_One_Has_Been_Passed_is_a_Violation(){
        Student bebe = mock(Student.class);
        Course math1 = new Course("4", "MATH1", 3);
        Course phys1 = new Course("8", "PHYS1", 3);
        Course phys2 = new Course("9", "PHYS2", 3).withPre(math1, phys1);
        Section phys2_1 = new Section(phys2, "01");
        EnrollmentList list1 = new EnrollmentList("TestList1", bebe);
        list1.addSections(phys2_1);
        when(bebe.calculateGPA()).thenReturn(15F);
        when(bebe.hasPassed(math1)).thenReturn(true);
        when(bebe.hasPassed(phys1)).thenReturn(false);
        assertThat(list1.checkEnrollmentRules())
                .isNotNull()
                .hasSize(1);
    }

}