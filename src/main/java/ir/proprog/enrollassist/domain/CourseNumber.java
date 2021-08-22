package ir.proprog.enrollassist.domain;

import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
final public class CourseNumber {
    final private String courseNumber;

    public CourseNumber() {
        this.courseNumber = "0000000";
    }

    public CourseNumber(String courseNumber) throws Exception {
        this.validate(courseNumber);
        this.courseNumber = courseNumber;
    }

    private void validate(String courseNumber) throws Exception {
        if (courseNumber.equals(""))
            throw new Exception("Course number cannot be empty.");
        try {
            Integer.parseInt(courseNumber);
        }catch (Exception exception) {
            throw new Exception("Course number must be number.");
        }
        if (courseNumber.length() != 7)
            throw new Exception("Course number must contain 7 numbers.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseNumber courseNumber = (CourseNumber) o;
        return this.courseNumber.equals(courseNumber.courseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.courseNumber);
    }

}
