package com.monaly.erpmonaly.application.DTOs.users;

import com.monaly.erpmonaly.domain.enums.UserRole;

public record CreateUserResponse(String username, String email, UserRole role) {
}
