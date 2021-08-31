package ir.proprog.enrollassist.controller.course;

import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.course.CourseNumber;
import lombok.Getter;
import java.util.*;

@Getter
public class CourseView {
    private Long courseId;
    private CourseNumber courseNumber;
    private String courseTitle;
    private int courseCredits;
    private Set<Long> prerequisites = new HashSet<>();

    public CourseView() {
    }

    public CourseView(Course course) {
        this.courseId = course.getId();
        this.courseNumber = course.getCourseNumber();
        this.courseTitle = course.getTitle();
        this.courseCredits = course.getCredits();
        if(!course.getPrerequisites().isEmpty())
            for(Course c : course.getPrerequisites())
                prerequisites.add(c.getId());
    }

    public CourseView(Course course, Set<Long> prerequisites) {
        this.courseId = course.getId();
        this.courseNumber = course.getCourseNumber();
        this.courseTitle = course.getTitle();
        this.courseCredits = course.getCredits();
        this.prerequisites = prerequisites;
    }
}
