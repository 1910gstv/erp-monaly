package com.monaly.erpmonaly.application.DTOs.users;

import com.monaly.erpmonaly.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateUserRequest(
        @NotBlank
        String username,

        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotNull
        UserRole role,

        @NotNull
        UUID companyId
) {};
