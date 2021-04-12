package academy.devdojo.youtube.security.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import academy.devdojo.youtube.core.model.ApplicationUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityContextUtil {
	private SecurityContextUtil() {}
	
	public static void setSecurityContext(SignedJWT signedJWT) {
		try {
			JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
			
			String username = jwtClaimsSet.getSubject();
			
			if(username == null)
				throw new JOSEException("Username missing from JWT");
			
			List<String> authorities = jwtClaimsSet.getStringListClaim("authorities");
			ApplicationUser user = ApplicationUser.builder()
					.id(jwtClaimsSet.getLongClaim("userId"))
					.username(username)
					.role(String.join(",", authorities))
					.build();
			
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, createAuthorities(authorities));
			auth.setDetails(signedJWT.serialize());
			
			SecurityContextHolder.getContext().setAuthentication(auth);
		}catch(Exception e) {
			log.error("Error setting security context", e);
			SecurityContextHolder.clearContext();
		}
	}
	
		private static List<SimpleGrantedAuthority> createAuthorities(List<String> authorities) {
			return authorities.stream()
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());			
		}
}
