package com.mariuszilinskas.vsp.apigateway.filter;

import com.mariuszilinskas.vsp.apigateway.dto.JwtPayload;
import com.mariuszilinskas.vsp.apigateway.service.JwtService;
import com.mariuszilinskas.vsp.apigateway.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final JwtService jwtService;

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (AppUtils.isPublicPath.test(request)) {
            return chain.filter(exchange);
        }

        var payloadOpt = jwtService.extractPayload(exchange);
        if (payloadOpt.isEmpty()) {
            logger.error("Access token is missing for a protected path");
            return onError(exchange, "Access token is required", HttpStatus.UNAUTHORIZED);
        }

        JwtPayload payload = payloadOpt.get();
        String principal = payload.userId();
        List<GrantedAuthority> authorities = extractAuthorities(payload);

        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        logger.info("Set security context {}", payload);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

    }

    private List<GrantedAuthority> extractAuthorities(JwtPayload payload) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (payload.roles() != null) {
            authorities.addAll(payload.roles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList());
        }
        if (payload.authorities() != null) {
            authorities.addAll(payload.authorities().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        }
        return authorities;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(httpStatus);
        DataBuffer dataBuffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer)).then(response.setComplete());
    }

}
