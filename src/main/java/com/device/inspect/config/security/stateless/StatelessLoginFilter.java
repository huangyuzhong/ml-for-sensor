package com.device.inspect.config.security.stateless;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

//	private final TokenAuthenticationService tokenAuthenticationService;
	private final LoginUserService loginUserService;
    private final Set<String> authorities;


	public StatelessLoginFilter(String urlMapping,
			LoginUserService loginUserService, AuthenticationManager authManager, Set<String> authorities) {
		super(new AntPathRequestMatcher(urlMapping));
		this.loginUserService = loginUserService;
//		this.tokenAuthenticationService = tokenAuthenticationService;
        this.authorities = authorities;
		setAuthenticationManager(authManager);

	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		String input = request.getInputStream().toString();
		Map<String,String[]> map2 =request.getParameterMap();
//		final LoginUser user = new ObjectMapper().readValue(request.getInputStream(), LoginUser.class);
		String name = "";
		String verify = "";
		if (null!=map2.get("name")&&map2.get("name").length>0)
			name = map2.get("name")[0];
		if (null!=map2.get("verify")&&map2.get("verify").length>0)
			verify = map2.get("verify")[0];

		final LoginUser user = new LoginUser();
		user.setUsername(name);
		user.setVerify(verify);

		final LoginUser authenticatedUser = loginUserService.loadUserByName(user.getUsername(),user.getVerify(), authorities);

        // Lookup the complete User object from the database and create an Authentication for it

        final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

		return userAuthentication;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain, Authentication authentication) throws IOException, ServletException {

        if(authentication instanceof UserAuthentication) {
            final UserAuthentication userAuthentication = (UserAuthentication)authentication;
            // Add the custom token as HTTP header to the response
//            tokenAuthenticationService.addAuthentication(response, userAuthentication);

            // Add the authentication to the Security context
            SecurityContextHolder.getContext().setAuthentication(userAuthentication);
        }
	}


}