package com.monaly.erpmonaly.application.usecases.products;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateProductUseCaseTest {
    private static final UUID COMPANY_ID = UUID.randomUUID();

    private ProductGateway productGateway;
    private CreateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        useCase = new CreateProductUseCase(productGateway);
    }

    @Test
    void savesProductForAuthenticatedCompanyAndReturnsCreatedProduct() {
        CreateProductRequest request = request("Product", "SKU-1", "25.50");
        UserEntity user = authenticatedUser(COMPANY_ID);
        Product saved = product(request, COMPANY_ID);
        when(productGateway.create(request, COMPANY_ID)).thenReturn(saved);

        Product result = useCase.create(request, user);

        assertSame(saved, result);
        verify(productGateway).create(request, COMPANY_ID);
    }

    @Test
    void allowsProductWithoutSku() {
        CreateProductRequest request = request("Product", null, "25.50");
        when(productGateway.create(request, COMPANY_ID)).thenReturn(product(request, COMPANY_ID));

        useCase.create(request, authenticatedUser(COMPANY_ID));

        verify(productGateway).create(request, COMPANY_ID);
    }

    @Test
    void translatesDuplicateSkuInSameCompanyToDuplicationException() {
        CreateProductRequest request = request("Product", "DUP-1", "25.50");
        when(productGateway.create(eq(request), eq(COMPANY_ID))).thenThrow(
                new DataIntegrityViolationException("duplicate", new RuntimeException("uq_products_company_sku"))
        );

        assertThrows(DuplicationException.class, () -> useCase.create(request, authenticatedUser(COMPANY_ID)));
    }

    @Test
    void rethrowsUnrelatedDataIntegrityViolationWithoutMisreportingSkuConflict() {
        CreateProductRequest request = request("Product", "SKU-1", "25.50");
        DataIntegrityViolationException failure = new DataIntegrityViolationException(
                "integrity failure", new RuntimeException("fk_products_company")
        );
        when(productGateway.create(any(CreateProductRequest.class), eq(COMPANY_ID))).thenThrow(failure);

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class,
                () -> useCase.create(request, authenticatedUser(COMPANY_ID))
        );

        assertSame(failure, thrown);
    }

    private UserEntity authenticatedUser(UUID companyId) {
        return new UserEntity(UUID.randomUUID(), "Admin", "admin@monaly.com", "hashed", UserRole.ADMIN, companyId);
    }

    private CreateProductRequest request(String name, String sku, String price) {
        return new CreateProductRequest(name, new BigDecimal(price), sku, "Description", "ACTIVE");
    }

    private Product product(CreateProductRequest request, UUID companyId) {
        return new Product(
                UUID.randomUUID(), companyId, request.name(), request.sku(), request.price(),
                request.description(), request.state(), OffsetDateTime.now(), OffsetDateTime.now(), null
        );
    }
}
