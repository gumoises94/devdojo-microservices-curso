package academy.devdojo.youtube.auth.security.filter;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import academy.devdojo.youtube.core.model.ApplicationUser;
import academy.devdojo.youtube.core.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authManager;
	private final JwtConfiguration jwtConfiguration;
	
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
		
		SignedJWT signedJWT =  createSignedJWT(auth);
		String encryptedToken = encryptToken(signedJWT);
		String jwtHeaderName = jwtConfiguration.getHeader().getName();
		
		log.info("Token generated successfully, adding it to the responde header");
		response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN, " + jwtHeaderName);
		response.addHeader(jwtHeaderName, jwtConfiguration.getHeader().getPrefix() + encryptedToken);
	}
	
	@SneakyThrows
	private SignedJWT createSignedJWT(Authentication auth) {
		log.info("Starting to create the signed JWT");
		
		ApplicationUser user = (ApplicationUser) auth.getPrincipal();
		JWTClaimsSet jwtClaimsSet = createJWTClaimSet(auth, user);
		KeyPair rsaKeys = generateKeyPair();
		
		log.info("Generating JWK from the RSA Keys");
		
		JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeys.getPublic())
			.keyID(UUID.randomUUID().toString())
			.build();
		JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
				.jwk(jwk)
				.type(JOSEObjectType.JWT)
				.build();
		
		SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
		
		log.info("Signing the token with the private RSA Key");
		
		RSASSASigner signer = new RSASSASigner(rsaKeys.getPrivate());
		signedJWT.sign(signer);
		
		
		log.info("Serialized token '{}'", signedJWT.serialize());
		return signedJWT;
	}
	
	private JWTClaimsSet createJWTClaimSet(Authentication auth, ApplicationUser user) {
		log.info("Creating JWT ClaimSet for '{}'", user);
		
		return new JWTClaimsSet.Builder()
				.subject(user.getUsername())
				.claim("authorities", auth.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList()))
				.issuer("http://academy.devdojo")
				.issueTime(new Date())
				.expirationTime(new Date(System.currentTimeMillis() + (jwtConfiguration.getExpiration() * 1000)))
				.build();
	}
	
	@SneakyThrows
	private KeyPair generateKeyPair() {
		log.info("Generating RSA 2048 Keys");
		
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		
		return generator.genKeyPair();
	}
	
	private String encryptToken(SignedJWT signedJWT) throws JOSEException {
		log.info("Starting the encryptToken method");
		
		DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());
		JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
				.contentType("JWT")
				.build();
		
		JWEObject jweObject = new JWEObject(jweHeader, new Payload(signedJWT));
		 
		 log.info("Encrypting token with the system's private key");
		 
		 jweObject.encrypt(directEncrypter);
		 
		 log.info("Token encrypted");
		 
		 return jweObject.serialize();
	}
}
