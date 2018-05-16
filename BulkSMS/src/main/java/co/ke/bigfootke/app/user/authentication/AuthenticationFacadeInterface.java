package co.ke.bigfootke.app.user.authentication;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacadeInterface {

	Authentication getAuthentication();
}