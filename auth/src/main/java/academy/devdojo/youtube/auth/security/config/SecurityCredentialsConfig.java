package academy.devdojo.youtube.auth.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import academy.devdojo.youtube.auth.security.filter.JwtUsernameAndPasswordAuthenticationFilter;
import academy.devdojo.youtube.core.property.JwtConfiguration;
import academy.devdojo.youtube.security.config.SecurityTokenConfig;
import academy.devdojo.youtube.security.token.creator.TokenCreator;

@EnableWebSecurity
public class SecurityCredentialsConfig extends SecurityTokenConfig {

	private final UserDetailsService userDetailsService;
	private final TokenCreator tokenCreator;


	public SecurityCredentialsConfig(JwtConfiguration jwtConfiguration, 
			@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
			TokenCreator tokenCreator) {
		super(jwtConfiguration);
		this.userDetailsService = userDetailsService;
		this.tokenCreator = tokenCreator;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfiguration, tokenCreator));
		
		super.configure(http);
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}



}
