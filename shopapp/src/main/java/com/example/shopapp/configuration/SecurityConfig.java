package com.example.shopapp.configuration;

import com.example.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    // Add your security configuration here

    // Define UserDetailsService Bean
    // The UserDetailsService will be used by the AuthenticationManagerBuilder to load the user by phone number
    @Bean
    public UserDetailsService userDetailsService() {
        // Return a lambda function that takes a phone number and returns a user object
        // This lambda function implements the Single Abstract Method (SAM) of the UserDetailsService interface
        // Go to the documentation of the UserDetailsService interface to see the SAM
        return phoneNumber -> {
            // Find the user by phone number
            // The return value of this lambda function is the UserDetails object
            // Therefore, we have to implement the UserDetails interface in the User class
            // Go to the User class to see the implementation of the UserDetails interface
            return userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "Cannot find user with phone number: "
                                    + phoneNumber));    // Return the user object
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Return a new instance of the BCryptPasswordEncoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Return a new instance of the CustomAuthenticationProvider
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager(); // Return the authentication manager
    }
}
