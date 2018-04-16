package co.ke.bigfootke.app.services.userdetails;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import co.ke.bigfootke.app.jpa.entities.Credentials;
import co.ke.bigfootke.app.jpa.entities.User;
import co.ke.bigfootke.app.jpa.implementations.CredentialsJpaImplementation;


public class CustomUserDetails extends User implements UserDetails{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	CredentialsJpaImplementation userAccessRepo;

	public CustomUserDetails(final User user) {
		super(user);
	}

	@Override
	public String getUsername() {
		return super.getEmail();
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
		Credentials cred = userAccessRepo.findByUserId(super.getUserId());
		return cred.isActive();
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		Credentials cred = userAccessRepo.findByUserId(super.getUserId());
		return AuthorityUtils.createAuthorityList(cred.getRole());
		
//		return AuthorityUtils.createAuthorityList("ADMIN");
		//return null;//getRole().v;
//				.stream()
//				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
//				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		Credentials cred = userAccessRepo.findByUserId(super.getUserId());
		String password = cred.getPassword();
		return password;
	}

}
