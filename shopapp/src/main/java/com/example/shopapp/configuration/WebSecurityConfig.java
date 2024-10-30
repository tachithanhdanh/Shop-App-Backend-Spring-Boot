package com.example.shopapp.configuration;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.example.shopapp.components.JwtAuthenticationEntryPoint;
import com.example.shopapp.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // disable CSRF
        // allow registration and login requests
        // allow GET requests to the /orders endpoint only for users with the role USER or ADMIN
        // allow POST requests to the /orders endpoint only for users with the role USER
        // allow PUT requests to the /orders endpoint only for users with the role ADMIN
        // allow DELETE requests to the /orders endpoint only for users with the role ADMIN
        // require authentication for all other requests
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix)
                            ).permitAll()

                            .requestMatchers(GET, // user can get orders by user ID
                                    String.format("%s/orders/**", apiPrefix))
                            .hasAnyRole("USER", "ADMIN")

                            .requestMatchers(POST, // user can create an order
                                    String.format("%s/orders/**", apiPrefix))
                            .hasRole("USER")

                            .requestMatchers(PUT, // only admin can update an order
                                    String.format("%s/orders/**", apiPrefix)).hasRole("ADMIN")

                            .requestMatchers(DELETE, // only admin can delete an order
                                    String.format("%s/orders/**", apiPrefix)).hasRole("ADMIN")

                            .anyRequest().authenticated();
                });
        return http.build();
    }
}
