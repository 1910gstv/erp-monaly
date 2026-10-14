package com.monaly.erpmonaly.application.DTOs.companies;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

public record CreateCompanyRequest(
    @NotBlank
    @Size(max = 150)
    String name,
    @NotBlank
    @CNPJ(message = "CNPJ inválido")
    String cnpj
){};
