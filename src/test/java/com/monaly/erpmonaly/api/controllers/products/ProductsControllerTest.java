package com.monaly.erpmonaly.api.controllers.products;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.usecases.products.CreateProductUseCase;
import com.monaly.erpmonaly.application.usecases.products.ListProductsByCompany;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import com.monaly.erpmonaly.infra.persistence.UserRepository;
import com.monaly.erpmonaly.infra.security.SecurityConfig;
import com.monaly.erpmonaly.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductsController.class)
@Import(SecurityConfig.class)
class ProductsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private ListProductsByCompany listProductsByCompany;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void rejectsUnauthenticatedProductCreation() throws Exception {
        mockMvc.perform(post("/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isUnauthorized());

        verify(createProductUseCase, never()).create(any(CreateProductRequest.class), any(UserEntity.class));
    }

    @Test
    void forwardsSearchFiltersAndPaginationForAuthenticatedUser() throws Exception {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UserEntity authenticatedUser = authenticatedUser(companyId);
        authenticate(authenticatedUser);

        Product found = new Product(
                productId, companyId, "Coffee", "SKU-1", new BigDecimal("12.50"),
                "Description", "ACTIVE", OffsetDateTime.now(), OffsetDateTime.now(), null
        );
        when(listProductsByCompany.listAllByCompany(
                eq(authenticatedUser), eq(2), eq(5), eq("coffee"), eq("SKU-1"),
                eq(productId), eq("ACTIVE")
        )).thenReturn(new PageImpl<>(List.of(found), PageRequest.of(2, 5), 11));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/products/all")
                        .header("Authorization", "Bearer test-token")
                        .param("page", "2")
                        .param("size", "5")
                        .param("name", "coffee")
                        .param("sku", "SKU-1")
                        .param("id", productId.toString())
                        .param("state", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(productId.toString()))
                .andExpect(jsonPath("$.content[0].companyId").value(companyId.toString()));

        verify(listProductsByCompany).listAllByCompany(
                authenticatedUser, 2, 5, "coffee", "SKU-1", productId, "ACTIVE"
        );
    }

    @Test
    void createsProductUsingAuthenticatedPrincipalAndReturnsIt() throws Exception {
        UUID companyId = UUID.randomUUID();
        UserEntity authenticatedUser = authenticatedUser(companyId);
        authenticate(authenticatedUser);
        Product created = product(companyId, "SKU-1");
        when(createProductUseCase.create(any(CreateProductRequest.class), eq(authenticatedUser))).thenReturn(created);

        mockMvc.perform(post("/products/create")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestWithCompanyId(UUID.randomUUID())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyId").value(companyId.toString()))
                .andExpect(jsonPath("$.sku").value("SKU-1"))
                .andExpect(jsonPath("$.price").value(12.50));

        verify(createProductUseCase).create(any(CreateProductRequest.class), eq(authenticatedUser));
    }

    @Test
    void allowsMissingOptionalSku() throws Exception {
        UUID companyId = UUID.randomUUID();
        UserEntity authenticatedUser = authenticatedUser(companyId);
        authenticate(authenticatedUser);
        when(createProductUseCase.create(any(CreateProductRequest.class), eq(authenticatedUser)))
                .thenReturn(product(companyId, null));

        mockMvc.perform(post("/products/create")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestWithoutSku()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").doesNotExist());

        verify(createProductUseCase).create(
                org.mockito.ArgumentMatchers.argThat(request -> request.sku() == null),
                eq(authenticatedUser)
        );
    }

    @Test
    void rejectsBlankNameBeforeCallingUseCase() throws Exception {
        performInvalidRequest(requestWith("name", "\" \""));
    }

    @Test
    void rejectsNameLongerThan150CharactersBeforeCallingUseCase() throws Exception {
        performInvalidRequest(requestWith("name", "\"" + "N".repeat(151) + "\""));
    }

    @Test
    void rejectsMissingNameBeforeCallingUseCase() throws Exception {
        performInvalidRequest("""
                {
                  "price": 12.50,
                  "sku": "SKU-1",
                  "description": "Description",
                  "state": "ACTIVE"
                }
                """);
    }

    @Test
    void rejectsNegativePriceBeforeCallingUseCase() throws Exception {
        performInvalidRequest(requestWith("price", "-0.01"));
    }

    @Test
    void rejectsMissingPriceBeforeCallingUseCase() throws Exception {
        performInvalidRequest("""
                {
                  "name": "Product",
                  "sku": "SKU-1",
                  "description": "Description",
                  "state": "ACTIVE"
                }
                """);
    }

    @Test
    void returnsConflictWhenSkuIsAlreadyUsedInAuthenticatedCompany() throws Exception {
        UserEntity authenticatedUser = authenticatedUser(UUID.randomUUID());
        authenticate(authenticatedUser);
        when(createProductUseCase.create(any(CreateProductRequest.class), eq(authenticatedUser)))
                .thenThrow(new DuplicationException("SKU already exists"));

        mockMvc.perform(post("/products/create")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("SKU already exists"));
    }

    private void performInvalidRequest(String request) throws Exception {
        UserEntity authenticatedUser = authenticatedUser(UUID.randomUUID());
        authenticate(authenticatedUser);

        mockMvc.perform(post("/products/create")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(createProductUseCase, never()).create(any(CreateProductRequest.class), any(UserEntity.class));
    }

    private void authenticate(UserEntity user) {
        when(tokenService.validateToken("test-token")).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
    }

    private UserEntity authenticatedUser(UUID companyId) {
        return new UserEntity(UUID.randomUUID(), "Admin", "admin@monaly.com", "hashed", UserRole.ADMIN, companyId);
    }

    private Product product(UUID companyId, String sku) {
        return new Product(
                UUID.randomUUID(), companyId, "Product", sku, new BigDecimal("12.50"),
                "Description", "ACTIVE", OffsetDateTime.now(), OffsetDateTime.now(), null
        );
    }

    private String validRequest() {
        return """
                {
                  "name": "Product",
                  "price": 12.50,
                  "sku": "SKU-1",
                  "description": "Description",
                  "state": "ACTIVE"
                }
                """;
    }

    private String validRequestWithoutSku() {
        return """
                {
                  "name": "Product",
                  "price": 12.50,
                  "description": "Description",
                  "state": "ACTIVE"
                }
                """;
    }

    private String validRequestWithCompanyId(UUID companyId) {
        return """
                {
                  "name": "Product",
                  "price": 12.50,
                  "sku": "SKU-1",
                  "description": "Description",
                  "state": "ACTIVE",
                  "companyId": "%s"
                }
                """.formatted(companyId);
    }

    private String requestWith(String field, String value) {
        String request = validRequest();
        if ("price".equals(field)) {
            return request.replace("\"price\": 12.50", "\"price\": " + value);
        }
        return request.replace("\"" + field + "\": \"Product\"", "\"" + field + "\": " + value);
    }
}
