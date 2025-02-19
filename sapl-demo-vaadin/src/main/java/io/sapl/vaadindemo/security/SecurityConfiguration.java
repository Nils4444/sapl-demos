package io.sapl.vaadindemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

import io.sapl.vaadin.base.VaadinAuthorizationSubscriptionBuilderService;
import io.sapl.vaadindemo.views.LoginView;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll();

		// Icons from the line-awesome addon
		http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll();
		super.configure(http);
		setLoginView(http, LoginView.class);
	}

	/**
	 * Demo UserDetailService which only provide two hard coded in memory users and
	 * their roles. NOTE: This should not be used in real world applications.
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails admin = User.withUsername("admin").password("{noop}admin").roles("Admin").build();
		UserDetails user  = User.withUsername("user").password("{noop}user").roles("USER").build();
		return new InMemoryUserDetailsManager(admin, user);
	}

	@Bean
	protected VaadinAuthorizationSubscriptionBuilderService vaadinAuthorizationSubscriptionBuilderService(
			ObjectMapper mapper) {
		var expressionHandler = new DefaultMethodSecurityExpressionHandler();
		return new VaadinAuthorizationSubscriptionBuilderService(expressionHandler, mapper);
	}
}
