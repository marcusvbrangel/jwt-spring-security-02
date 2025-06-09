package com.mvbr.jwtspringsecurity02.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
    @NotBlank(message = "O campo username é obrigatório")
    @Email(message = "O campo username deve ser um e-mail válido")
    String username,
    @NotBlank(message = "O campo password é obrigatório")
    String password
) {}
