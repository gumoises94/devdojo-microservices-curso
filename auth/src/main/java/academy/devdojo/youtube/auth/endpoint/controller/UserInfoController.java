package academy.devdojo.youtube.auth.endpoint.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.devdojo.youtube.core.model.ApplicationUser;

@RestController
@RequestMapping("user")
public class UserInfoController {

	@GetMapping(path = "info", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApplicationUser> getUserInfo(Principal principal) {
		ApplicationUser user = castAndGetPrincipal(principal);
		
		return new ResponseEntity<ApplicationUser>(user, HttpStatus.OK);
	}
	
	private ApplicationUser castAndGetPrincipal(Principal principal) {
		return (ApplicationUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
	}
	
}
