package com.monaly.erpmonaly;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.usecases.products.CreateProductUseCase;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.CompanyRepository;
import com.monaly.erpmonaly.infra.persistence.ProductRepository;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
class ProductCreationIT {
    @Container
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine")
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
    private ProductRepository productRepository;

    @Autowired
    private CreateProductUseCase createProductUseCase;

    private UUID firstCompanyId;
    private UUID secondCompanyId;

    @BeforeEach
    void createCompanies() {
        productRepository.deleteAllInBatch();
        firstCompanyId = UUID.randomUUID();
        secondCompanyId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        companyRepository.saveAndFlush(new CompanyEntity(
                firstCompanyId, "Company One", taxId(), now, now
        ));
        companyRepository.saveAndFlush(new CompanyEntity(
                secondCompanyId, "Company Two", taxId(), now, now
        ));
    }

    @Test
    void duplicateSkuConflictsWithinSameCompanyButIsAllowedInAnotherCompany() {
        createProductUseCase.create(request("SKU-SHARED"), authenticatedUser(firstCompanyId));

        assertThrows(DuplicationException.class,
                () -> createProductUseCase.create(request("SKU-SHARED"), authenticatedUser(firstCompanyId)));

        var otherCompanyProduct = createProductUseCase.create(
                request("SKU-SHARED"), authenticatedUser(secondCompanyId)
        );
        assertEquals(secondCompanyId, otherCompanyProduct.companyId());
        assertEquals(2, productRepository.count());
    }

    @Test
    void allowsMultipleProductsWithoutSkuInSameCompany() {
        createProductUseCase.create(request(null), authenticatedUser(firstCompanyId));
        createProductUseCase.create(request(null), authenticatedUser(firstCompanyId));

        assertEquals(2, productRepository.count());
        assertNull(productRepository.findAll().get(0).getSku());
    }

    private CreateProductRequest request(String sku) {
        return new CreateProductRequest("Product", new BigDecimal("10.00"), sku, "Description", "ACTIVE");
    }

    private UserEntity authenticatedUser(UUID companyId) {
        return new UserEntity(UUID.randomUUID(), "Admin", UUID.randomUUID() + "@monaly.com",
                "hashed", UserRole.ADMIN, companyId);
    }

    private String taxId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 14);
    }
}
