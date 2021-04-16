package academy.devdojo.youtube.security.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import academy.devdojo.youtube.core.model.ApplicationUser;
import academy.devdojo.youtube.security.user.UserDetailsImpl;


public class BaseSpringSecurityWebAuxTestConfig {

    public UserDetailsService userDetailsService() {
        UserDetails commonUser = new UserDetailsImpl(ApplicationUser.builder()
        		.id(1L)
        		.username("commonUser")
        		.password("123456")
        		.role("USER")
        		.build());
        
        UserDetails adminUser = new UserDetailsImpl(ApplicationUser.builder()
        		.id(2L)
        		.username("adminUser")
        		.password("123456")
        		.role("ADMIN")
        		.build());
        
        UserDetails guestUser = new UserDetailsImpl(ApplicationUser.builder()
        		.id(3L)
        		.username("guestUser")
        		.password("123456")
        		.role("GUEST")
        		.build());
        
        return new InMemoryUserDetailsImpl(Arrays.asList(
                commonUser, adminUser, guestUser
        ));
    }
    
    private static class InMemoryUserDetailsImpl implements UserDetailsService {
    	
    	private List<UserDetails> users;
    	
    	public InMemoryUserDetailsImpl(List<UserDetails> users) {
    		this.users = users != null ? users : new ArrayList<UserDetails>();
    	}

		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			return users.stream()
					.filter(user -> user.getUsername().equals(username))
					.findFirst()
					.orElse(null);
		}
    }
}
