package org.demo;

import org.demo.domain.UserRepository;
import org.demo.shared.advicehandlers.EmailAdviceHandler;
import org.demo.shared.advicehandlers.SimpleLoggingAdviceHandler;
import org.demo.shared.marshalling.AuthenticationMapper;
import org.demo.shared.marshalling.HttpServletRequestMapper;
import org.demo.shared.marshalling.PatientMapper;
import org.demo.shared.obligationhandlers.CoffeeObligationHandler;
import org.demo.shared.obligationhandlers.EmailObligationHandler;
import org.demo.shared.obligationhandlers.SimpleLoggingObligationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import io.sapl.api.pdp.advice.AdviceHandlerService;
import io.sapl.api.pdp.mapping.SaplMapper;
import io.sapl.api.pdp.obligation.ObligationHandlerService;
import io.sapl.pep.pdp.advice.SimpleAdviceHandlerService;
import io.sapl.pep.pdp.mapping.SimpleSaplMapper;
import io.sapl.pep.pdp.obligation.SimpleObligationHandlerService;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/css/**/*.css").permitAll().anyRequest().authenticated().and().formLogin()
				.loginPage("/login").permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/login")
				.permitAll().and().httpBasic().and().csrf().disable();
		http.headers().frameOptions().disable();

	}

	@Bean
	public AuthenticationManager authManager(UserRepository userRepo) {
		return new AuthManager(userRepo);
	}

	@Bean
	public ObligationHandlerService getObligationHandlers() {
		ObligationHandlerService sohs = new SimpleObligationHandlerService();
		sohs.register(new EmailObligationHandler());
		sohs.register(new CoffeeObligationHandler());
		sohs.register(new SimpleLoggingObligationHandler());
		return sohs;
	}

	@Bean
	public AdviceHandlerService setAdviceHandlers() {
		AdviceHandlerService sahs = new SimpleAdviceHandlerService();
		sahs.register(new EmailAdviceHandler());
		sahs.register(new SimpleLoggingAdviceHandler());
		return sahs;
	}

	@Bean
	public SaplMapper getSaplMapper() {
		SaplMapper saplMapper = new SimpleSaplMapper();
		saplMapper.register(new AuthenticationMapper());
		saplMapper.register(new HttpServletRequestMapper());
		saplMapper.register(new PatientMapper());
		return saplMapper;
	}

}
