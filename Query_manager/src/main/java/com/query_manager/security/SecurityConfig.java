package com.query_manager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationFailureHandler customFailureHandler;

    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    @Autowired
    @Qualifier("studentDetailsDelegate")
    private UserDetailsService delegate;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/reg/login**",
                    "/process-login",
                    "/activate",
                    "/forgot-password",
                    "/reset-password",
                    "/css/**",
                    "/error",
                    "/error/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/reg/login")
                .loginProcessingUrl("/process-login")
                .successHandler(customLoginSuccessHandler)
                .failureHandler(customFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/reg/login?logout")
                .permitAll()
            )
            .sessionManagement(session -> session
                .invalidSessionUrl("/reg/login?session=invalid")
                .maximumSessions(1)
                .expiredUrl("/reg/login?session=expired")
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(delegate)
                   .passwordEncoder(passwordEncoder())
                   .and()
                   .build();
    }
}
