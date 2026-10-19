package com.monaly.erpmonaly.infra.gateways.users;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.domain.entities.users.User;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import com.monaly.erpmonaly.infra.persistence.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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

        UserEntity savedEntity;
        try {
            savedEntity = userRepository.saveAndFlush(userEntity);
        } catch (DataIntegrityViolationException exception) {
            if (isEmailUniquenessViolation(exception)) {
                throw new DuplicationException("O email informado já existe, tente outro");
            }
            throw exception;
        }

        return new CreateUserResponse(
                savedEntity.getUsername(),
                savedEntity.getEmail(),
                savedEntity.getRole()
        );

    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    private boolean isEmailUniquenessViolation(Throwable exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraintViolation
                    && "uq_users_email".equals(constraintViolation.getConstraintName())) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
