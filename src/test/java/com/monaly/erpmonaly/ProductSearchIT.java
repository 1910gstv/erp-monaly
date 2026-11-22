package com.monaly.erpmonaly;

import com.monaly.erpmonaly.application.usecases.products.ListProductsByCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.CompanyRepository;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;
import com.monaly.erpmonaly.infra.persistence.ProductRepository;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class ProductSearchIT {
    @Container
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine");

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
    private ListProductsByCompanyUseCase listProductsByCompanyUseCase;

    private UUID companyId;
    private UUID otherCompanyId;

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
        companyId = UUID.randomUUID();
        otherCompanyId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        companyRepository.saveAndFlush(new CompanyEntity(
                companyId, "Company One", taxId(), now, now
        ));
        companyRepository.saveAndFlush(new CompanyEntity(
                otherCompanyId, "Company Two", taxId(), now, now
        ));
    }

    @Test
    void searchesByPartialNameSkuAndStateWithoutReturningAnotherCompanyProducts() {
        OffsetDateTime now = OffsetDateTime.now();
        ProductEntity matchingProduct = saveProduct(
                UUID.randomUUID(), companyId, "Organic Coffee", "SKU-1", "ACTIVE", now, null
        );
        saveProduct(UUID.randomUUID(), companyId, "Organic Tea", "SKU-2", "ACTIVE", now, null);
        saveProduct(UUID.randomUUID(), otherCompanyId, "Organic Coffee", "SKU-1", "ACTIVE", now, null);

        Page<Product> result = listProductsByCompanyUseCase.listAllByCompany(
                authenticatedUser(companyId), 0, 10, "COFFEE", "SKU-1", null, "ACTIVE"
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(matchingProduct.getId(), result.getContent().get(0).id());
    }

    @Test
    void searchesByIdOnlyWithinAuthenticatedCompany() {
        ProductEntity ownProduct = saveProduct(
                UUID.randomUUID(), companyId, "Product One", "SKU-1", "ACTIVE", OffsetDateTime.now(), null
        );
        ProductEntity otherProduct = saveProduct(
                UUID.randomUUID(), otherCompanyId, "Product Two", "SKU-2", "ACTIVE", OffsetDateTime.now(), null
        );

        Page<Product> ownResult = listProductsByCompanyUseCase.listAllByCompany(
                authenticatedUser(companyId), 0, 10, null, null, ownProduct.getId(), null
        );
        Page<Product> otherCompanyResult = listProductsByCompanyUseCase.listAllByCompany(
                authenticatedUser(companyId), 0, 10, null, null, otherProduct.getId(), null
        );
        Page<Product> missingResult = listProductsByCompanyUseCase.listAllByCompany(
                authenticatedUser(companyId), 0, 10, null, null, UUID.randomUUID(), null
        );

        assertEquals(List.of(ownProduct.getId()), ownResult.getContent().stream().map(Product::id).toList());
        assertEquals(0, otherCompanyResult.getTotalElements());
        assertEquals(0, missingResult.getTotalElements());
    }

    @Test
    void filtersByStateAndExcludesSoftDeletedProducts() {
        OffsetDateTime now = OffsetDateTime.now();
        ProductEntity inactiveProduct = saveProduct(
                UUID.randomUUID(), companyId, "Inactive Product", "SKU-1", "INACTIVE", now, null
        );
        saveProduct(UUID.randomUUID(), companyId, "Active Product", "SKU-2", "ACTIVE", now, null);
        saveProduct(UUID.randomUUID(), companyId, "Deleted Product", "SKU-3", "INACTIVE", now, now);

        Page<Product> result = listProductsByCompanyUseCase.listAllByCompany(
                authenticatedUser(companyId), 0, 10, null, null, null, "INACTIVE"
        );

        assertEquals(List.of(inactiveProduct.getId()), result.getContent().stream().map(Product::id).toList());
    }

    @Test
    void paginatesWithStableOrderingAndReturnsPageMetadata() {
        OffsetDateTime baseTime = OffsetDateTime.parse("2026-01-01T00:00:00Z");
        UUID oldestId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID middleId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID newestId = UUID.fromString("00000000-0000-0000-0000-000000000003");

        saveProduct(oldestId, companyId, "Oldest", "SKU-1", "ACTIVE", baseTime, null);
        saveProduct(middleId, companyId, "Middle", "SKU-2", "ACTIVE", baseTime.plusMinutes(1), null);
        saveProduct(newestId, companyId, "Newest", "SKU-3", "ACTIVE", baseTime.plusMinutes(1), null);

        Page<Product> firstPage = listProductsByCompanyUseCase.listAllByCompany(
                authenticatedUser(companyId), 0, 2, null, null, null, null
        );
        Page<Product> secondPage = listProductsByCompanyUseCase.listAllByCompany(
                authenticatedUser(companyId), 1, 2, null, null, null, null
        );

        assertEquals(3, firstPage.getTotalElements());
        assertEquals(2, firstPage.getTotalPages());
        assertEquals(List.of(newestId, middleId), firstPage.getContent().stream().map(Product::id).toList());
        assertEquals(List.of(oldestId), secondPage.getContent().stream().map(Product::id).toList());
    }

    private ProductEntity saveProduct(
            UUID id,
            UUID productCompanyId,
            String name,
            String sku,
            String state,
            OffsetDateTime createdAt,
            OffsetDateTime deletedAt
    ) {
        return productRepository.saveAndFlush(new ProductEntity(
                id,
                productCompanyId,
                name,
                sku,
                new BigDecimal("10.00"),
                "Description",
                state,
                createdAt,
                createdAt,
                deletedAt
        ));
    }

    private UserEntity authenticatedUser(UUID authenticatedCompanyId) {
        return new UserEntity(
                UUID.randomUUID(),
                "Admin",
                UUID.randomUUID() + "@monaly.com",
                "hashed",
                UserRole.ADMIN,
                authenticatedCompanyId
        );
    }

    private String taxId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 14);
    }
}
