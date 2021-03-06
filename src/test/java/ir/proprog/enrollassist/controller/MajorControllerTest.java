package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.major.MajorController;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.utils.TestCourseBuilder;
import ir.proprog.enrollassist.domain.utils.TestMajorBuilder;
import ir.proprog.enrollassist.repository.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.CoreMatchers.anyOf;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MajorController.class)
public class MajorControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private MajorRepository majorRepository;
    private Major major1, major2;
    private Course course1, course2;
    JSONObject request;
    JSONObject response;

    void setUpWithTestBuilders() throws ExceptionList{
        major1 = new TestMajorBuilder()
                .majorName("CE")
                .majorNumber("8101")
                .build();
        major2 = new TestMajorBuilder()
                .majorName("CHEM")
                .majorNumber("8102")
                .build();
        course1 = new TestCourseBuilder()
                .courseNumber("1111111")
                .title("C1")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build();
        course2 = new TestCourseBuilder()
                .courseNumber("2222222")
                .title("C2")
                .credits(4)
                .graduateLevel("Undergraduate")
                .build();
    }

    void setUpWithMock() {
        major1 = mock(Major.class);
        request = new JSONObject();
        response = new JSONObject();
        given(majorRepository.findByMajorName("EE")).willReturn(java.util.Optional.ofNullable(major1));

    }
    @Test
    public void All_majors_are_returned_correctly() throws Exception{
        setUpWithTestBuilders();
        given(majorRepository.findAll()).willReturn(List.of(major1, major2));
        mvc.perform(get("/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].majorName", is("CE")))
                .andExpect(jsonPath("$[0].majorNumber", is("8101")))
                .andExpect(jsonPath("$[1].majorName", is("CHEM")))
                .andExpect(jsonPath("$[1].majorNumber", is("8102")));
    }

    @Test
    public void Major_with_acceptable_values_is_added_correctly() throws Exception{
        setUpWithMock();
        request.put("majorName", "CHEM");
        request.put("majorNumber", "8102");
        request.put("faculty", "Engineering");

        mvc.perform(post("/majors")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.majorName", is("CHEM")))
                .andExpect(jsonPath("$.majorNumber", is("8102")));
    }

    @Test
    public void Major_with_a_name_that_already_exists_is_not_added() throws Exception{
        setUpWithMock();
        request.put("majorName", "EE");
        request.put("majorNumber", "8101");
        request.put("faculty", "Engineering");

        response.put("1", "Major with name EE exists.");

        mvc.perform(post("/majors")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }

    @Test
    public void Major_name_and_major_number_cannot_be_empty() throws Exception{
        setUpWithMock();
        request.put("majorName", "");
        request.put("majorNumber", "");
        request.put("faculty", "Engineering");

        response.put("1", "Major name can not be empty.");
        response.put("2", "Major number can not be empty.");


        mvc.perform(post("/majors")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }
}
