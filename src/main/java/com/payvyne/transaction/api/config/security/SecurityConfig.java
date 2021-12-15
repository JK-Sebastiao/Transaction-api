package com.payvyne.transaction.api.config.security;

import com.payvyne.transaction.api.authentication.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private UserService userService;
    private JwtTokenFilter jwtTokenFilter;

    @Value("${springdoc.api-docs.path}")
    private String restApiDocPath;
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;

    public SecurityConfig(UserService userService, JwtTokenFilter jwtTokenFilter) {
        this.userService = userService;
        this.jwtTokenFilter = jwtTokenFilter;

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> userService.getUserByUsername(username));
    }

    @Override
    protected void  configure(HttpSecurity http) throws Exception {
         http.csrf().disable()
                 .sessionManagement()
                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                 .exceptionHandling()
                 .authenticationEntryPoint(
                         (request, response, exception) -> {
                             logger.error("Unauthorized request - {}", exception.getMessage());
                             response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
                         }).and()
                 .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                 .authorizeRequests()
                 .antMatchers("/").permitAll()
                 .antMatchers(String.format("%s/**", restApiDocPath)).permitAll()
                 .antMatchers(String.format("%s/**", swaggerPath)).permitAll()
                 .antMatchers(HttpMethod.POST,"/users", "/auth/**").permitAll()
                 .antMatchers("/actuator/**").permitAll()
                 .anyRequest().authenticated();


    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
