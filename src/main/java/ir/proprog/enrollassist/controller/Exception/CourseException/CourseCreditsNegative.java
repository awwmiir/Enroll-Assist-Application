package ir.proprog.enrollassist.controller.Exception.CourseException;

public class CourseCreditsNegative extends CourseException {
    public CourseCreditsNegative() {}

    @Override
    public String toString() {return "Course credit units cannot be negative.";}
}