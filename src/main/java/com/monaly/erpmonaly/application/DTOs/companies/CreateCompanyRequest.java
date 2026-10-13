package com.monaly.erpmonaly.application.DTOs.companies;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public record CreateCompanyRequest(
    @NotBlank
    String name,
    @NotBlank
    @CNPJ(message = "CNPJ inválido")
    String cnpj
){};
