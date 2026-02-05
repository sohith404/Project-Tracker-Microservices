package org.ptb.trackerservice.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Look for the header the Gateway injected
        String email = request.getHeader("X-User-Email");
        String role = request.getHeader("X-User-Role");

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Reconstruct authorities so @PreAuthorize works
            List<SimpleGrantedAuthority> authorities = (role != null)
                    ? Collections.singletonList(new SimpleGrantedAuthority(role))
                    : Collections.emptyList();

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    email, null, authorities);

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
