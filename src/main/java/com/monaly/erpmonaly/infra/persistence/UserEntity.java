package com.monaly.erpmonaly.infra.persistence;

import com.monaly.erpmonaly.domain.enums.UserRole;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String username;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    protected UserEntity() {
        // Construtor exigido pelo JPA
    }
    public UserEntity(
            UUID id,
            String username,
            String email,
            String password,
            UserRole role,
            UUID companyId
    ) {
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

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public UUID getCompanyId(){
        return companyId;
    }
}