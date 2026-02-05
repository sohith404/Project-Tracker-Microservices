package org.ptb.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final String secretKey="MySuperSecretKeyForProjectTrackerSystem12345";

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
                return chain.filter(exchange);
            }
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization Header");
            }
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Unauthorized");
            }
            String token = authHeader.substring(7);
            try {
                // FIX: Use io.jsonwebtoken.Claims
                Claims claims = Jwts.parser()
                        .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                // Inject the REAL data from the token
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Email", claims.getSubject())
                        .header("X-User-Role", (String) claims.get("role"))
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                return onError(exchange, "Invalid Token");
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}