package com.mvbr.jwtspringsecurity02.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ExampleRequest(
    @NotBlank(message = "O campo message é obrigatório")
    String message
) {}
