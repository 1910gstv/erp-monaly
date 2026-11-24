package com.monaly.erpmonaly.application.usecases.products;

import com.monaly.erpmonaly.application.DTOs.EditProductRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductEntityMapper;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

class EditProductUseCaseTest {
    private ProductGateway productGateway;
    private EditProductUseCase useCase;
    private final ProductEntityMapper mapper = new ProductEntityMapper();

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        useCase = new EditProductUseCase(productGateway, mapper);
    }

    @Test
    void editsAllowedFieldsAndPreservesStateAndAuditDates() throws Exception {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
        Product active = product(productId, companyId, "Old", "SKU-1", "ACTIVE", createdAt);
        EditProductRequest request = new EditProductRequest(
                "New", new BigDecimal("20.00"), "SKU-2", "New description"
        );
        when(productGateway.findByIdAndCompany(productId, companyId)).thenReturn(active);
        when(productGateway.update(any(ProductEntity.class), org.mockito.ArgumentMatchers.eq(productId),
                org.mockito.ArgumentMatchers.eq(companyId))).thenAnswer(invocation -> {
                    ProductEntity entity = invocation.getArgument(0);
                    return mapper.toDomain(entity);
        });

        Product result = useCase.edit(authenticatedUser(companyId), request, productId);

        assertEquals("New", result.name());
        assertEquals("SKU-2", result.sku());
        assertEquals(new BigDecimal("20.00"), result.price());
        assertEquals("New description", result.description());
        assertEquals("ACTIVE", result.state());
        assertEquals(createdAt, result.createdAt());
        assertNull(result.deletedAt());
        assertNotNull(result.updatedAt());
        verify(productGateway).findByIdAndCompany(productId, companyId);
    }

    @Test
    void deactivatesActiveProductWithoutDeletingItsRecord() throws Exception {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product active = product(productId, companyId, "Product", "SKU-1", "ACTIVE", OffsetDateTime.now().minusDays(1));
        when(productGateway.findByIdAndCompany(productId, companyId)).thenReturn(active);
        when(productGateway.update(any(ProductEntity.class), org.mockito.ArgumentMatchers.eq(productId),
                org.mockito.ArgumentMatchers.eq(companyId))).thenAnswer(invocation -> {
                    ProductEntity entity = invocation.getArgument(0);
                    return mapper.toDomain(entity);
        });

        Product result = useCase.deactivate(authenticatedUser(companyId), productId);

        assertEquals("INACTIVE", result.state());
        assertEquals(active.id(), result.id());
        assertEquals(active.companyId(), result.companyId());
        assertNull(result.deletedAt());
        assertNotNull(result.updatedAt());
        assertFalse("ACTIVE".equals(result.state()));
    }

    @Test
    void reactivatesInactiveProductExplicitly() throws Exception {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product inactive = product(productId, companyId, "Product", "SKU-1", "INACTIVE", OffsetDateTime.now().minusDays(1));
        when(productGateway.findByIdAndCompany(productId, companyId)).thenReturn(inactive);
        when(productGateway.update(any(ProductEntity.class), org.mockito.ArgumentMatchers.eq(productId),
                org.mockito.ArgumentMatchers.eq(companyId))).thenAnswer(invocation -> {
                    ProductEntity entity = invocation.getArgument(0);
                    return mapper.toDomain(entity);
        });

        Product result = useCase.reactivate(authenticatedUser(companyId), productId);

        assertEquals("ACTIVE", result.state());
        assertNotNull(result.updatedAt());
    }

    @Test
    void inactiveProductIsNotEligibleAsAnActiveSaleItem() {
        UUID companyId = UUID.randomUUID();
        Product inactive = product(UUID.randomUUID(), companyId, "Product", "SKU-1", "INACTIVE", OffsetDateTime.now());

        assertFalse("ACTIVE".equals(inactive.state()));
    }

    @Test
    void doesNotDeactivateProductFromAnotherCompany() throws Exception {
        UUID authenticatedCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(productGateway.findByIdAndCompany(productId, authenticatedCompanyId))
                .thenThrow(new EntityNotFoundException("Produto não encontrado"));

        assertThrows(
                EntityNotFoundException.class,
                () -> useCase.deactivate(authenticatedUser(authenticatedCompanyId), productId)
        );
        verify(productGateway, never()).update(any(ProductEntity.class),
                org.mockito.ArgumentMatchers.eq(productId),
                org.mockito.ArgumentMatchers.eq(authenticatedCompanyId));
    }

    private UserEntity authenticatedUser(UUID companyId) {
        return new UserEntity(UUID.randomUUID(), "Admin", "admin@monaly.com",
                "hashed", UserRole.ADMIN, companyId);
    }

    private Product product(UUID productId, UUID companyId, String name, String sku, String state,
                            OffsetDateTime createdAt) {
        return new Product(
                productId, companyId, name, sku, new BigDecimal("10.00"),
                "Description", state, createdAt, createdAt, null
        );
    }
}
