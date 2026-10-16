package com.monaly.erpmonaly.infra.gateways.users;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import com.monaly.erpmonaly.infra.persistence.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRepositoryGatewayTest {

    private UserRepository userRepository;
    private UserRepositoryGateway gateway;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        gateway = new UserRepositoryGateway(userRepository);
    }

    @Test
    void translatesEmailUniqueConstraintViolationToDuplicationException() {
        DataIntegrityViolationException persistenceException = integrityViolation("uq_users_email");
        when(userRepository.save(any(UserEntity.class))).thenThrow(persistenceException);

        assertThrows(DuplicationException.class, () -> gateway.save(request()));
    }

    @Test
    void propagatesOtherIntegrityViolationsWithoutClassifyingThemAsEmailConflicts() {
        DataIntegrityViolationException persistenceException = integrityViolation("users_company_id_fkey");
        when(userRepository.save(any(UserEntity.class))).thenThrow(persistenceException);

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class,
                () -> gateway.save(request())
        );

        assertSame(persistenceException, thrown);
    }

    private DataIntegrityViolationException integrityViolation(String constraintName) {
        ConstraintViolationException cause = new ConstraintViolationException(
                "Constraint violated",
                new SQLException("Constraint violated"),
                constraintName
        );
        return new DataIntegrityViolationException("Could not persist user", cause);
    }

    private CreateUserRequest request() {
        return new CreateUserRequest(
                "Monaly Admin",
                "admin@monaly.com",
                "$2a$hashed-password",
                UserRole.ADMIN,
                "11222333000181",
                "Mercado Monaly",
                UUID.randomUUID()
        );
    }
}
