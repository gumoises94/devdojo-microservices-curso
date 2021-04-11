package academy.devdojo.youtube.auth.security.filter;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;

import academy.devdojo.youtube.core.model.ApplicationUser;
import academy.devdojo.youtube.core.property.JwtConfiguration;
import academy.devdojo.youtube.security.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authManager;
	private final JwtConfiguration jwtConfiguration;
	private final TokenCreator tokenCreator;
	
	@Override
	@SneakyThrows
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		log.info("Atempting authentication...");
		
		ApplicationUser user = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
		
		if(user == null) 
			throw new UsernameNotFoundException("Unable to retrieve username or password");
		
		log.info("Creating the authentication object for the user '{}' and calling UserDetailServiceImpl loadByUsername", user.getUsername());
		
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
				new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), Collections.emptyList());
		
		usernamePasswordAuthenticationToken.setDetails(user);
		
		return authManager.authenticate(usernamePasswordAuthenticationToken);
	}
	
	@Override
	@SneakyThrows
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		
		log.info("Authentication was successful for the user '{}', generating JWE token", auth.getName());	
		
		SignedJWT signedJWT =  tokenCreator.createSignedJWT(auth);
		String encryptedToken = tokenCreator.encryptToken(signedJWT);
		String jwtHeaderName = jwtConfiguration.getHeader().getName();
		
		log.info("Token generated successfully, adding it to the responde header");
		response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN, " + jwtHeaderName);
		response.addHeader(jwtHeaderName, jwtConfiguration.getHeader().getPrefix() + encryptedToken);
	}
}
