package com.monaly.erpmonaly.domain.entities.users;

import com.monaly.erpmonaly.domain.enums.UserRole;

public record User(Long id, String username, String email, String password, UserRole role) {
    public User(String username, String email, String password, UserRole role) {
        this(null, username, email, password, role);
    }
}