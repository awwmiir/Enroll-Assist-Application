package ir.proprog.enrollassist.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // as required by JPA, don't use it in your code
@Getter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String studentNumber;
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<StudyRecord> grades = new HashSet<>();

    public Student(@NonNull String studentNumber, @NonNull String name) {
        if (studentNumber.equals(""))
            throw new IllegalArgumentException("Student number cannot be empty");
        if (name.equals(""))
            throw new IllegalArgumentException("Student must have a name");
        this.studentNumber = studentNumber;
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
            if (sr.getCourse().equals(course) && sr.getGrade() >= 10)
                return true;
        }
        return false;
    }

    public Student setGrade(String term, Course course, double grade) {
        grades.add(new StudyRecord(term, course, grade));
        return this;
    }

    public int getTotalTakenCredits() {
        return grades.stream().mapToInt(e -> e.getCourse().getCredits()).sum();
    }

    public float calculateGPA() {
        int credits = 0;
        float sum = 0;
        for (StudyRecord sr : grades) {
            sum += sr.getCourse().getCredits() * sr.getGrade();
            credits += sr.getCourse().getCredits();
        }
        if(credits == 0) return 0F;

        return (float) (Math.round(sum / credits * 100.0) / 100.0);
    }

    public List<Course> getPassedCourses(){
        List<Course> list = new ArrayList<>();
        for (StudyRecord sr : grades)
            if (sr.getGrade() >= 10)
                list.add(sr.getCourse());
        return list;
    }

}
