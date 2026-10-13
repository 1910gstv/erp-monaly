package com.monaly.erpmonaly.infra.gateways.users;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.domain.entities.users.User;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import com.monaly.erpmonaly.infra.persistence.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepositoryGateway implements UserGateway {
    private final UserRepository userRepository;

    public UserRepositoryGateway(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public CreateUserResponse save(CreateUserRequest request) {
        UUID id = UUID.randomUUID();

        User user = new User(
                id,
                request.username(),
                request.email(),
                request.password(),
                request.role(),
                request.companyId()
        );
        UserEntity userEntity = new UserEntity(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getCompanyId()
        );

        UserEntity savedEntity = userRepository.save(userEntity);
        return new CreateUserResponse(
                savedEntity.getUsername(),
                savedEntity.getEmail(),
                savedEntity.getRole()
        );

    }
}
