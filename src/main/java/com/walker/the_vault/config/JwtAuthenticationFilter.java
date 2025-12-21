package com.walker.the_vault.config;

import com.walker.the_vault.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal (
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Check if the request has a token header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the token (Remove "Bearer " prefix)
        jwt = authHeader.substring(7);

        // 3. Extract username from token
        username = jwtService.extractUsername(jwt); // <--- WE NEED TO ADD THIS METHOD to JwtService!

        // 4. If user is found and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from Database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 5. Validate token
            if (jwtService.isTokenValid(jwt, userDetails)) { // <--- AND THIS ONE!

                // 6. Create the "Pass" (Authentication Token)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 7. Update the Security Context (The "Logged In" state)
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }

        // 8. Continue the chain
        filterChain.doFilter(request, response);
    }
}
