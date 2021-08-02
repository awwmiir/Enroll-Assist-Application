package ir.proprog.enrollassist.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SectionTest {
    @Test
    public void None_number_character_is_not_valid_for_section_number() {
        Course course = mock(Course.class);
        String exception = "";
        try {
            Section section = new Section(course, "e34");
        } catch (IllegalArgumentException illegalArgumentException) {
            exception = illegalArgumentException.getMessage();
        }
        assertEquals(exception, "Section number must be number");
    }
}