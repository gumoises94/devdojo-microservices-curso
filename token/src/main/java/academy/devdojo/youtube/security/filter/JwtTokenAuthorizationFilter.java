package academy.devdojo.youtube.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jwt.SignedJWT;

import academy.devdojo.youtube.core.property.JwtConfiguration;
import academy.devdojo.youtube.security.token.converter.TokenConverter;
import academy.devdojo.youtube.security.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {
	
	protected final JwtConfiguration jwtConfiguration;
	protected final TokenConverter tokenConverter;
	
	@Override
	public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(jwtConfiguration.getHeader().getName());
		
		if(isAuthorizationHeader(header)) {
			String token = getTokenFromAuthorizationHeader(header);
			
			SignedJWT signedJWT = 
					StringUtils.equalsIgnoreCase("signed", jwtConfiguration.getType()) ? validateToken(token) : decryptAndValidateToken(token);
			
			SecurityContextUtil.setSecurityContext(signedJWT);
		}
		
		filterChain.doFilter(request, response);
	}
	
	private boolean isAuthorizationHeader(String header) {
		if(header != null && header.startsWith(jwtConfiguration.getHeader().getName()))
			return true;
		
		return false;
	}
	
	private String getTokenFromAuthorizationHeader(String authorizationHeader) {
		return authorizationHeader.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();
	}
	
	@SneakyThrows
	private SignedJWT decryptAndValidateToken(String encryptedToken) {
		String signedToken = tokenConverter.decryptToken(encryptedToken);
				
		return validateToken(signedToken);
	}
	

	@SneakyThrows
	private SignedJWT validateToken(String signedToken) {
		tokenConverter.validateTokenSignature(signedToken);
		
		return SignedJWT.parse(signedToken);
	}

}
