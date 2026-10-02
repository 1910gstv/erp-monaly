package com.monaly.erpmonaly.application.DTOs.companies;

import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(
    @NotBlank
    String name
){};
