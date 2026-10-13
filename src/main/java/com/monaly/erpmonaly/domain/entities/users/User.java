package com.monaly.erpmonaly.domain.entities.users;

import com.monaly.erpmonaly.domain.enums.UserRole;

import java.util.UUID;

public record User(UUID id, String username, String email, String password, UserRole role, UUID companyId) {
    public User(UUID id, String username, String email, String password, UserRole role, UUID companyId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.companyId = companyId;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {return role;}

    public UUID getCompanyId() { return companyId;}

    public String getPassword() {return password;}

}