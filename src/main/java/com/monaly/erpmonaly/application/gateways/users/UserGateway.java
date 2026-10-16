package com.monaly.erpmonaly.application.gateways.users;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;

public interface UserGateway {
    CreateUserResponse save(CreateUserRequest request);
    boolean existsByEmail(String email);
}
