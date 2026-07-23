package com.auctions.auction_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auctions.auction_service.security.HeaderAuthenticationFilter;
import com.auctions.auction_service.security.JwtAccessDeniedHandler;
import com.auctions.auction_service.security.JwtAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final HeaderAuthenticationFilter headerAuthenticationFilter;
	private final JwtAuthenticationEntryPoint authenticationEntryPoint;
	private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
        	.csrf(csrf -> csrf.disable())
        	.exceptionHandling(ex -> ex
        			.authenticationEntryPoint(authenticationEntryPoint)
        			.accessDeniedHandler(accessDeniedHandler))
        	.authorizeHttpRequests(auth -> auth
        			.requestMatchers(
                            "/auction/vehicle"
    					).permitAll()
        			.anyRequest()
        			.authenticated()
                )
        	.addFilterBefore(
                    headerAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
