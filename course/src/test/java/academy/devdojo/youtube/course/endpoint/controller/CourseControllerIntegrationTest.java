package academy.devdojo.youtube.course.endpoint.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import academy.devdojo.youtube.core.model.Course;
import academy.devdojo.youtube.course.security.config.SpringSecurityWebAuxTestConfig;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityWebAuxTestConfig.class
)
@AutoConfigureMockMvc
public class CourseControllerIntegrationTest {
	
	@Autowired
    private MockMvc mockMvc;
	
	private List<Course> inMemoryCourses;
	
	@BeforeEach
	public void setUp() {
		inMemoryCourses = Arrays.asList(
				Course.builder()
					.id(1L)
					.title("Java")
					.build(),
				Course.builder()
					.id(2L)
					.title("Spring")
					.build(),
				Course.builder()
					.id(3L)
					.title("MySQL")
					.build()
		);
	}
	
	@WithUserDetails("adminUser")
	@Test
	public void givenAdminUser_whenListingCourses_ThenOkTest() throws Exception {
		ResultActions ra = mockMvc.perform(get("/v1/admin/course"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
		
		for(int i = 0; i<inMemoryCourses.size(); i++) {
			Course course = inMemoryCourses.get(i);
			ra
				.andExpect(jsonPath(String.format("$.content[%d].id", i)).value(course.getId()))
				.andExpect(jsonPath(String.format("$.content[%d].title", i)).value(course.getTitle()));
		}
	}
	
	@WithUserDetails("commonUser")
	@Test
	public void givenUnauthorizedUser_whenListingCourses_thenShouldFailWithForbiddenResponseTest() throws Exception {
		mockMvc.perform(get("/v1/admin/course"))
				.andExpect(status().isForbidden());
	}
}
