package com.monaly.erpmonaly;

import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.CompanyRepository;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import com.monaly.erpmonaly.infra.persistence.UserRepository;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class UserCompanyIsolationIT {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withStartupTimeout(Duration.ofMinutes(3));

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGateway userGateway;

    @Test
    void repositoryQueriesReturnOnlyUsersFromRequestedCompany() {
        UUID firstCompanyId = UUID.randomUUID();
        UUID secondCompanyId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        companyRepository.saveAllAndFlush(List.of(
                new CompanyEntity(firstCompanyId, "Company One", "11111111111111", now, now),
                new CompanyEntity(secondCompanyId, "Company Two", "22222222222222", now, now)
        ));

        UserEntity firstCompanyUser = new UserEntity(
                UUID.randomUUID(), "User One", UUID.randomUUID() + "@monaly.com", "hashed",
                UserRole.USER, firstCompanyId
        );
        UserEntity secondCompanyUser = new UserEntity(
                UUID.randomUUID(), "User Two", UUID.randomUUID() + "@monaly.com", "hashed",
                UserRole.USER, secondCompanyId
        );
        userRepository.saveAllAndFlush(List.of(firstCompanyUser, secondCompanyUser));

        var firstCompanyUsers = userGateway.findAll(firstCompanyId);
        var secondCompanyUsers = userGateway.findAll(secondCompanyId);
        var allUsers = userGateway.findAll();

        assertEquals(List.of(firstCompanyUser.getId()),
                firstCompanyUsers.stream().map(user -> user.id()).toList());
        assertEquals(List.of(secondCompanyUser.getId()),
                secondCompanyUsers.stream().map(user -> user.id()).toList());
        assertEquals(2, allUsers.size());
    }
}
