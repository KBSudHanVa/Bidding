package com.auctions.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.auctions.api_gateway.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        // PUBLIC APIs
        if (
                path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/auth/refresh")
        ) {

            return exchange.getResponse().setComplete();
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        boolean valid = jwtUtil.validateAccessToken(token);

        if (!valid) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        Claims claims = jwtUtil.extractClaims(token);

        String email = claims.getSubject();

        String role = claims.get("role", String.class);

        ServerWebExchange modifiedExchange = exchange
                .mutate()
                .request(builder -> builder
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                )
                .build();

        return chain.filter(modifiedExchange);
//        		exchange.getChain().filter(modifiedExchange);
    }
}