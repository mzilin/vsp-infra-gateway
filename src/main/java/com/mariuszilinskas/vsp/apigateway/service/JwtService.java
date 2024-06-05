package com.mariuszilinskas.vsp.apigateway.service;

import com.mariuszilinskas.vsp.apigateway.dto.JwtPayload;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

public interface JwtService {

    Optional<JwtPayload> extractPayload(ServerWebExchange exchange);

}
