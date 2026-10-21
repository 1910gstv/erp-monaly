package com.monaly.erpmonaly.infra.persistence;

import com.monaly.erpmonaly.domain.enums.UserRole;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity implements UserDetails { // <-- 1. Adicione implements UserDetails aqui

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

    // --- Métodos do UserDetails ---

    @Override
    public String getUsername() {
        return email; // O Spring Security usa o e-mail para identificar o login
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna as roles/permissões do usuário mapeadas para o Spring Security
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // --- Seus Getters tradicionais ---

    public UUID getId() {
        return id;
    }

    public String getName() {
        return username; // Retorna o nome da pessoa caso precise exibir na tela
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public UUID getCompanyId() {
        return companyId;
    }
}