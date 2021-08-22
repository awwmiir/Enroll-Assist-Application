package ir.proprog.enrollassist.domain;

import com.google.common.annotations.VisibleForTesting;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.CourseView;
import ir.proprog.enrollassist.controller.SectionView;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Embedded
    private StudentNumber studentNumber;
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<StudyRecord> grades = new HashSet<>();
    @ManyToOne
    Major major;

    @OneToMany
    private List<Student> pending = new ArrayList<>();
    @OneToMany
    private List<Student> requested = new ArrayList<>();
    @OneToMany
    private List<Student> friends = new ArrayList<>();
    @OneToMany
    private List<Student> blocked = new ArrayList<>();


    public Student(@NonNull String studentNumber, @NonNull String name) {
        this.studentNumber = new StudentNumber(studentNumber);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentNumber.equals(student.studentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber);
    }

    public boolean hasPassed(Course course) {
        for (StudyRecord sr : grades) {
            if (sr.getCourse().equals(course) && sr.getGrade().isPassingGrade())
                return true;
        }
        return false;
    }

    public Student setGrade(String term, Course course, double grade) throws ExceptionList {
        grades.add(new StudyRecord(term, course, grade));
        return this;
    }

    public int getTotalTakenCredits() {
        return grades.stream().mapToInt(e -> e.getCourse().getCredits()).sum();
    }

    public Grade calculateGPA() {
        double sum = grades.stream().mapToDouble(StudyRecord::weightedScore).sum();
        int credits = grades.stream().mapToInt(sr -> sr.getCourse().getCredits()).sum();
        if (credits == 0) return Grade.ZERO;
        try {
            return new Grade(sum / credits);
        } catch (Exception e) {
            return Grade.ZERO;
        }
    }

    @VisibleForTesting
    List<Course> getTakeableCourses(){
        List<Course> passed = grades.stream().filter(sr -> sr.getGrade().isPassingGrade()).map(StudyRecord::getCourse).collect(Collectors.toList());
        List<Course> takeable  = new ArrayList<>();
        List<Course> notPassed = new ArrayList<>(major.getCourses());
        notPassed.removeAll(passed);
        for(Course c : notPassed)
            if(c.canBeTakenBy(this).isEmpty())
                takeable.add(c);
        return takeable;
    }

    public List<Section> getTakeableSections(Iterable<Section> allSections){
        List<Course> takeableCourses = getTakeableCourses();
        List<Section> takeableSections = new ArrayList<>();
        for (Section section: allSections)
            for(Course course: takeableCourses)
                if(section.courseIsEqualTo(course)) {
                    takeableSections.add(section);
                    break;
                }
        return takeableSections;
    }

    public void setMajor(Major major) {
        this.major = major;
    }
}
