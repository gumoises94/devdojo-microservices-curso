package academy.devdojo.youtube.security.token.converter;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import academy.devdojo.youtube.core.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Slf4j
public class TokenConverter {
	private final JwtConfiguration jwtConfiguration;
	
	@SneakyThrows
	public String decryptToken(String encryptedToken) {
		log.info("Decrypting token");
		
		JWEObject jweObject = JWEObject.parse(encryptedToken);
		DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
		
		jweObject.decrypt(directDecrypter);
		
		log.info("Token decrypted, returning signed token");
		
		return jweObject.getPayload().toSignedJWT().serialize();
	}
	
	@SneakyThrows
	public void validateTokenSignature(String signedToken) {
		log.info("Validating token signature");
		SignedJWT signedJWT = SignedJWT.parse(signedToken);
		
		log.info("Token parsed! Retrieving public key from signed token");
		RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
		
		log.info("Public key retrieved... validating signature");
		
		if(!signedJWT.verify(new RSASSAVerifier(publicKey)))
			throw new AccessDeniedException("Invalid token signature");
		
		log.info("The token has a valid signature");
	}
}
