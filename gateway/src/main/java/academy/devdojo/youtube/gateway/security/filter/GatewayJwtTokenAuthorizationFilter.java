package academy.devdojo.youtube.gateway.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;

import com.netflix.zuul.context.RequestContext;
import com.nimbusds.jwt.SignedJWT;

import academy.devdojo.youtube.core.property.JwtConfiguration;
import academy.devdojo.youtube.security.filter.JwtTokenAuthorizationFilter;
import academy.devdojo.youtube.security.token.converter.TokenConverter;
import academy.devdojo.youtube.security.util.SecurityContextUtil;
import lombok.SneakyThrows;

public class GatewayJwtTokenAuthorizationFilter extends JwtTokenAuthorizationFilter {

	public GatewayJwtTokenAuthorizationFilter(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
		super(jwtConfiguration, tokenConverter);
	}

	@Override
	@SneakyThrows
	public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(jwtConfiguration.getHeader().getName());
		
		if(isAuthorizationHeader(header)) {
			String token = getTokenFromAuthorizationHeader(header);
			
			String signedToken = tokenConverter.decryptToken(token);
			tokenConverter.validateTokenSignature(signedToken);
			
			SecurityContextUtil.setSecurityContext(SignedJWT.parse(signedToken));
			
			if(jwtConfiguration.getType().equalsIgnoreCase("signed"))
				RequestContext.getCurrentContext().addZuulRequestHeader("Authorization", jwtConfiguration.getHeader().getPrefix() + signedToken);
			
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
	
}
