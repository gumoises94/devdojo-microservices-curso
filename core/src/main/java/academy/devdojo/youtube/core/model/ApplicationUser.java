package academy.devdojo.youtube.core.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author William Suane
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ApplicationUser implements AbstractEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull(message = "The field 'username' is mandatory")
    @Column(nullable = false)
    private String username;
    @NotNull(message = "The field 'password' is mandatory")
    @Column(nullable = false)
    private String password;
    @NotNull(message = "The field 'role' is mandatory")
    @Column(nullable = false)
    @Builder.Default
    private String role = "USER";
    
    public ApplicationUser(ApplicationUser user) {
    	this.id = user.getId();
    	this.username = user.getUsername();
    	this.password = user.getPassword();
    	this.role = user.getRole();
    }
}
