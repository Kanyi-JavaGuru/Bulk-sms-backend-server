package co.ke.bigfootke.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter{
	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				.authenticationManager(authenticationManager)
				.allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.GET)
				;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_USER')")
				.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
		.withClient("test01")
		.secret("test01")
//		.authorizedGrantTypes("client_credentials", "password","authorization_code","refresh_token")
		/**password for first time sign in/login or expiration of refresh token
		 * refresh token after login**/
		.authorizedGrantTypes( "password")
		.authorities("ROLE_ADMIN", "ROLE_USER")
		.scopes("read", "write", "trust")
		.resourceIds("oauth2-resource")
		.accessTokenValiditySeconds(3600)//expires in 1 hour
		.refreshTokenValiditySeconds(604800);//expires after 7 days
	}
}
