package com.example.shopapp.filters;

import com.example.shopapp.components.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    // userDetailsService is used to load the user details from the database
    // it is injected via the constructor, and it is a bean defined in the SecurityConfig class
    private final JwtUtils jwtUtils;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Check if the request has a token
        // If the token is valid, set the authentication in the SecurityContext
        // Continue with the filter chain
        // enable bypass for some requests
        if (isBypassToken(request)) {
            filterChain.doFilter(request, response); // enable bypass for some requests
            return;
        }
        try {
            // check if the request has a token
            // if the token is valid, set the authentication in the SecurityContext
            // continue with the filter chain
            // if the token is invalid, return an error response
            // if the token is missing, return an error response
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            String token = authHeader.substring(
                    "Bearer ".length()); // remove "Bearer " from the token
            String phoneNumber = jwtUtils.extractPhoneNumber(token);
            // if the phone number is not null and the authentication is null then we set the authentication
            // via the userDetailsService
            if (phoneNumber != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                // get the user details from the userDetailsService
                var userDetails = userDetailsService.loadUserByUsername(phoneNumber);
                // validate the token
                if (jwtUtils.validateToken(token, userDetails)) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET")
        );
        for (Pair<String, String> bypassToken : bypassTokens) {
            if (request.getRequestURI().equals(bypassToken.getFirst())
                    && request.getMethod().equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
}
