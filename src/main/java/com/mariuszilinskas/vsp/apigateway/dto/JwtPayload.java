package com.mariuszilinskas.vsp.apigateway.dto;

import java.util.Date;
import java.util.List;

public record JwtPayload (
    String userId,
    List<String> roles,
    List<String> authorities,
    Date expiry
){}
