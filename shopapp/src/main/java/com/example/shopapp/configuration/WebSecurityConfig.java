package com.example.shopapp.configuration;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.example.shopapp.components.JwtAuthenticationEntryPoint;
import com.example.shopapp.filters.JwtTokenFilter;
import com.example.shopapp.models.Role;
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
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Value("${api.prefix}")
    private String apiPrefix;

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

                            .requestMatchers(GET, // user and admin can get categories
                                    String.format("%s/categories**", apiPrefix))
                            .hasAnyRole(Role.USER, Role.ADMIN)

                            .requestMatchers(POST, // only admin can create a category
                                    String.format("%s/categories/**", apiPrefix))
                            .hasRole(Role.ADMIN)

                            .requestMatchers(PUT, // only admin can update a category
                                    String.format("%s/categories/**", apiPrefix))
                            .hasRole(Role.ADMIN)

                            .requestMatchers(DELETE, // only admin can delete a category
                                    String.format("%s/categories/**", apiPrefix))
                            .hasRole(Role.ADMIN)

                            .requestMatchers(GET, // user and admin can get products
                                    String.format("%s/products**", apiPrefix))
                            .hasAnyRole(Role.USER, Role.ADMIN)

                            .requestMatchers(GET, // user and admin can get a product by ID
                                    String.format("%s/products/**", apiPrefix))
                            .hasAnyRole(Role.USER, Role.ADMIN)

                            .requestMatchers(POST, // only admin can create a product
                                    String.format("%s/products/**", apiPrefix))
                            .hasRole(Role.ADMIN)

                            .requestMatchers(PUT, // only admin can update a product
                                    String.format("%s/products/**", apiPrefix))
                            .hasRole(Role.ADMIN)

                            .requestMatchers(DELETE, // only admin can delete a product
                                    String.format("%s/products/**", apiPrefix))
                            .hasRole(Role.ADMIN)

                            .requestMatchers(GET, // user can get orders by user ID
                                    String.format("%s/orders/**", apiPrefix))
                            .hasAnyRole(Role.USER, Role.ADMIN)

                            .requestMatchers(POST, // user can create an order
                                    String.format("%s/orders/**", apiPrefix))
                            .hasRole(Role.USER)

                            .requestMatchers(PUT, // only admin can update an order
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)

                            .requestMatchers(DELETE, // only admin can delete an order
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)

                            .requestMatchers(GET, // user can get orders by user ID
                                    String.format("%s/order_details/**", apiPrefix))
                            .hasAnyRole(Role.USER, Role.ADMIN)

                            .requestMatchers(POST, // user can create an order
                                    String.format("%s/order_details/**", apiPrefix))
                            .hasRole(Role.USER)

                            .requestMatchers(PUT, // only admin can update an order
                                    String.format("%s/order_details/**", apiPrefix))
                            .hasRole(Role.ADMIN)

                            .requestMatchers(DELETE, // only admin can delete an order
                                    String.format("%s/order_details/**", apiPrefix))
                            .hasRole(Role.ADMIN)

                            .anyRequest().authenticated();
                });
        return http.build();
    }
}
