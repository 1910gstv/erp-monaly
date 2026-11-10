package com.monaly.erpmonaly.application.gateways.users;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.domain.entities.users.User;

import java.util.List;
import java.util.UUID;

public interface UserGateway {
    CreateUserResponse save(CreateUserRequest request);
    boolean existsByEmail(String email);
    List<User> findAll(UUID company_id);
}
