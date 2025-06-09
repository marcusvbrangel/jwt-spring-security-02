package com.mvbr.jwtspringsecurity02.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String nome,
    BigDecimal preco,
    UUID donoId,
    String donoUsername
) {}
