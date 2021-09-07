package ir.proprog.enrollassist.domain.program;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.major.Major;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @ManyToOne
    protected Major major;
    protected GraduateLevel graduateLevel;
    @Getter
    @ManyToMany
    protected Set<Course> courses = new HashSet<>();
    @Embedded
    protected CreditRange creditRange;

    public Program(Major major, String graduateLevel, int minimum, int maximum) throws ExceptionList {
        this.major = major;
        ExceptionList exceptionList = new ExceptionList();
        try {
            this.graduateLevel = GraduateLevel.valueOf(graduateLevel);
        } catch (Exception e) { exceptionList.addNewException(new Exception("Graduate level is not valid.")); }
        try {
            this.creditRange = new CreditRange(minimum, maximum);
        } catch (Exception e) { exceptionList.addNewException(e); }
        if (exceptionList.hasException())
            throw exceptionList;
    }

    public void addCourse(Course ... course){
        this.courses.addAll(Arrays.asList(course));
    }
}
