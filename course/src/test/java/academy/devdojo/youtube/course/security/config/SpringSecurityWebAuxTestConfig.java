package academy.devdojo.youtube.course.security.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

import academy.devdojo.youtube.security.config.BaseSpringSecurityWebAuxTestConfig;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig extends BaseSpringSecurityWebAuxTestConfig {

	

	@Bean
	@Primary
	@Override
	public UserDetailsService userDetailsService() {
		return super.userDetailsService();
	}
	
	
}
