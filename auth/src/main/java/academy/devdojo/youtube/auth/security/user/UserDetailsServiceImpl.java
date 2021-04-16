package academy.devdojo.youtube.auth.security.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import academy.devdojo.youtube.core.model.ApplicationUser;
import academy.devdojo.youtube.core.repository.ApplicationUserRepository;
import academy.devdojo.youtube.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final ApplicationUserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		log.info("Searching in the DB for user by username '{}'", username);
		ApplicationUser user = repository.findByUsername(username);
		
		if(user == null)
			throw new UsernameNotFoundException(String.format("User not found '%s'", username));
		else
			log.info("ApplicationUser found '{}'", user);
			
		
		return new UserDetailsImpl(user);
	}
}
