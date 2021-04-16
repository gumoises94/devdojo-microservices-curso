package academy.devdojo.youtube.auth.endpoint.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import academy.devdojo.youtube.auth.security.config.SpringSecurityWebAuxTestConfig;
import academy.devdojo.youtube.core.model.ApplicationUser;




@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityWebAuxTestConfig.class
)
@AutoConfigureMockMvc
public class UserInfoControllerIntegrationTest {
	
	@Autowired
    private MockMvc mockMvc;
	
	private ApplicationUser user;
	
	@WithUserDetails("adminUser")
	@Test
	public void givenAdminUser_whenGettingUserInfo_ThenShouldReturnUserTest() throws Exception {
		user = ApplicationUser.builder()
        		.username("adminUser")
        		.role("ADMIN")
        		.build();
		
		performGetAndExpectOk();
	}
	
	private void performGetAndExpectOk() throws Exception {
		mockMvc.perform(get("/user/info"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andExpect(jsonPath("$.username").value(user.getUsername()))
			.andExpect(jsonPath("$.role").value(user.getRole()));
	}
	
	@WithUserDetails("commonUser")
	@Test
	public void givenCommonUser_whenGettingUserInfo_ThenShouldReturnUserTest() throws Exception {
		user = ApplicationUser.builder()
        		.username("commonUser")
        		.role("USER")
        		.build();
		
		
		performGetAndExpectOk();
	}
	
	@WithUserDetails("guestUser")
	@Test
	public void givenUnauthorizedUser_whenGettingUserInfo_thenShouldFailWithForbiddenResponseTest() throws Exception {
		mockMvc.perform(get("/user/info"))
				.andExpect(status().isForbidden());
	}
	
}
