package com.monaly.erpmonaly;

import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.CompanyRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class CompanyRepositoryIT {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private Flyway flyway;

    @Test
    void appliesMigrationsAndPersistsCompany() {
        assertTrue(flyway.info().applied().length >= 2, "Flyway should apply the database migrations");

        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        CompanyEntity company = new CompanyEntity(
                id,
                "Mercado Monaly",
                "12345678000199",
                now,
                now
        );

        companyRepository.saveAndFlush(company);

        CompanyEntity savedCompany = companyRepository.findById(id).orElseThrow();
        assertEquals("Mercado Monaly", savedCompany.getName());
        assertEquals("12345678000199", savedCompany.getCnpj());
    }
}
