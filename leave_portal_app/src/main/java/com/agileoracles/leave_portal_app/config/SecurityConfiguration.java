package com.agileoracles.leave_portal_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/h2-console/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .csrf(csrf ->
                        csrf.ignoringRequestMatchers(
                                "/api/leave/upload",
                                "/h2-console/**"
                        )
                )
                .headers(headers ->
                        headers.frameOptions(frame ->
                                frame.sameOrigin()
                        )
                )
                .oauth2Login(Customizer.withDefaults());

        return http.build();
    }
}