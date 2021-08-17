package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CourseMajorView {
    private Long courseId;
    private String courseNumber;
    private String courseTitle;
    private int courseCredits;
    private final Set<Long> prerequisites = new HashSet<>();
    private Set<Long> majors = new HashSet<>();

    public CourseMajorView() {
    }

    public CourseMajorView(Course course, Set<Long> prerequisites, Set<Long> majors) {
        this.courseId = course.getId();
        this.courseNumber = course.getCourseNumber();
        this.courseTitle = course.getTitle();
        this.courseCredits = course.getCredits();
        if(!course.getPrerequisites().isEmpty())
            for(Course c : course.getPrerequisites())
                prerequisites.add(c.getId());
        this.majors = majors;
    }
}
