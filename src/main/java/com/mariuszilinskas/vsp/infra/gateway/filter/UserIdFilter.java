package com.mariuszilinskas.vsp.infra.gateway.filter;

import com.mariuszilinskas.vsp.infra.gateway.service.JwtServiceImpl;
import com.mariuszilinskas.vsp.infra.gateway.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserIdFilter implements WebFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(UserIdFilter.class);
    private final JwtServiceImpl jwtService;

    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String requestURI = exchange.getRequest().getURI().getPath();

        if (requestURI.contains(AppUtils.USER_ID)) {
            UUID userId = jwtService.extractUserId(exchange);
            String modifiedURI = requestURI.replace(AppUtils.USER_ID, userId.toString());
            logger.info("Converting _USER_ID_ path to [userId: '{}']", userId);

            return chain.filter(exchange.mutate().request(
                    exchange.getRequest().mutate().path(modifiedURI).build()
            ).build());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
