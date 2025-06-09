package com.mvbr.jwtspringsecurity02.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductCreateRequest(
    @NotBlank(message = "O campo nome é obrigatório")
    String nome,
    @NotNull(message = "O campo preco é obrigatório")
    BigDecimal preco
) {}
