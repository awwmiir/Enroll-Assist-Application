package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
@RestController
@RequestMapping("/courses")
public class CourseController {
    private CourseRepository courseRepository;
    List<CourseRuleViolation> courseRuleViolations = new ArrayList<>();

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public Iterable<CourseView> all() {
        return StreamSupport.stream(courseRepository.findAll().spliterator(), false).map(CourseView::new).collect(Collectors.toList());
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public CourseView addNewCourse(@RequestBody CourseView courseView){

        Course newCourse = new Course(courseView.getCourseNumber(), courseView.getCourseTitle(), courseView.getCourseCredits());
        ValidateCourseNumber(newCourse.getCourseNumber());
        ValidateCourseTitle(newCourse.getTitle());

        for(Long L : courseView.getPrerequisites()){
            Course prerequisite = courseRepository.findById(L)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prerequisite course not found"));
            newCourse.withPre(prerequisite);
        }
        courseRepository.save(newCourse);
        return new CourseView(newCourse);
    }

    private void ValidateCourseNumber(String courseNumber){
        if(courseNumber.equals(""))
            this.courseRuleViolations.add(new CourseNumberEmpty());
        else{
            try {
                Integer.parseInt(courseNumber);
                if(courseRepository.FindCourseByCourseNumber(courseNumber).isPresent())
                    this.courseRuleViolations.add(new CourseNumberExists());
            } catch (NumberFormatException numberFormatException) {
                this.courseRuleViolations.add(new WrongCourseNumberFormat());
            }
        }
    }

    private void ValidateCourseTitle(String title) {
        if (title.equals(""))
            this.courseRuleViolations.add(new CourseTitleEmpty());
    }

    @GetMapping("/{id}")
    public CourseView one(@PathVariable Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        return new CourseView(course);
    }
}
