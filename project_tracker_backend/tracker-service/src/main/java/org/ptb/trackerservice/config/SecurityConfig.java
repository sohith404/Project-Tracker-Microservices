package org.ptb.trackerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final InternalAuthFilter internalAuthFilter;

    // Inject the filter we created in Step 2
    public SecurityConfig(InternalAuthFilter internalAuthFilter) {
        this.internalAuthFilter = internalAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF because we are stateless (using JWT/Headers)
                .csrf(csrf -> csrf.disable())
                // 2. Define which URLs are protected
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Every call to Tracker must be authenticated
                )
                // 3. Make it Stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 4. THE MAGIC: Add your custom filter before the standard ones
                .addFilterBefore(internalAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
