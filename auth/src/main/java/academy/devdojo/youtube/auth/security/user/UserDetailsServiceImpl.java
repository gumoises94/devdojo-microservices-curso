package academy.devdojo.youtube.auth.security.user;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import academy.devdojo.youtube.core.model.ApplicationUser;
import academy.devdojo.youtube.core.repository.ApplicationUserRepository;
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
			
		
		return new CustomUserDetails(user);
	}
	
	private static class CustomUserDetails extends ApplicationUser implements UserDetails {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CustomUserDetails(ApplicationUser user) {
			super(user);
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + this.getRole());
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}
}
