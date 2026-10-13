package com.monaly.erpmonaly.application.usecases.users;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.domain.entities.users.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCase {
    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder){
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    public CreateUserResponse create(CreateUserRequest request){
        String hashedPassword = passwordEncoder.encode(request.password());
        
        CreateUserRequest finalUser = new CreateUserRequest(
                request.username(),
                request.email(),
                hashedPassword,
                request.role(),
                request.companyId()
        );
      return userGateway.save(finalUser);
    };
}
