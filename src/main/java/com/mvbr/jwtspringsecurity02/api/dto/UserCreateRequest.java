package com.mvbr.jwtspringsecurity02.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record UserCreateRequest(
    @NotBlank(message = "O campo username é obrigatório")
    @Email(message = "O campo username deve ser um e-mail válido")
    String username,
    @NotBlank(message = "O campo password é obrigatório")
    String password,
    @NotNull(message = "O campo roles é obrigatório")
    Set<String> roles
) {}
