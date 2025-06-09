package com.mvbr.jwtspringsecurity02.api.dto;

public record AuthResponse(
    String token,
    String username,
    String[] roles
) {}
