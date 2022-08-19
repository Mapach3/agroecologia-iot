package com.unla.agroecologiaiot.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.unla.agroecologiaiot.filters.AuthenticationFilter;
import com.unla.agroecologiaiot.filters.AuthorizationFilter;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;
import com.unla.agroecologiaiot.repositories.SessionRepository;
import com.unla.agroecologiaiot.services.ITokenService;
import com.unla.agroecologiaiot.services.implementation.ApplicationUserService;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.unla.agroecologiaiot.constants.SecurityConstants;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private ApplicationUserService userService;
    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private ITokenService tokenService;

    public SecurityConfiguration(ApplicationUserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
                .antMatchers(SecurityConstants.SWAGGER_URL_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(getAuthenticationFilter())
                .addFilter(new AuthorizationFilter(authenticationManager(), applicationUserRepository))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(SecurityConstants.REFRESH_TOKEN_URL);
        // .antMatchers(SecurityConstants.SIGN_UP);
    }


    public AuthenticationFilter getAuthenticationFilter() throws Exception {
        final AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager(),
                applicationUserRepository, sessionRepository, tokenService);
        authFilter.setFilterProcessesUrl(SecurityConstants.LOGIN_URL);
        return authFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

}
