package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditCompanyUseCaseTest {

    private static final UUID COMPANY_ID = UUID.randomUUID();
    private static final String FORMATTED_CNPJ = "11.222.333/0001-81";
    private static final String NORMALIZED_CNPJ = "11222333000181";

    private CompanyGateway companyGateway;
    private EditCompanyUseCase useCase;

    @BeforeEach
    void setUp() {
        companyGateway = mock(CompanyGateway.class);
        useCase = new EditCompanyUseCase(companyGateway);
    }

    @Test
    void companyAdminCanEditItsCompany() {
        UserEntity admin = authenticatedUser(UserRole.ADMIN, COMPANY_ID);
        CreateCompanyRequest request = new CreateCompanyRequest("Updated Company", FORMATTED_CNPJ);
        Company updated = new Company(COMPANY_ID, "Updated Company", NORMALIZED_CNPJ);
        when(companyGateway.editCompany(eq(COMPANY_ID), any(CreateCompanyRequest.class))).thenReturn(updated);

        Company result = useCase.editCompany(request, admin);

        assertEquals(updated, result);
        verify(companyGateway).editCompany(
                COMPANY_ID,
                new CreateCompanyRequest("Updated Company", NORMALIZED_CNPJ));
    }

    @Test
    void nonAdminCannotEditCompany() {
        UserEntity user = authenticatedUser(UserRole.USER, COMPANY_ID);

        assertThrows(AccessDeniedException.class,
                () -> useCase.editCompany(
                        new CreateCompanyRequest("Updated Company", FORMATTED_CNPJ), user));

        verify(companyGateway, never()).editCompany(any(), any());
    }

    @Test
    void updatesCompanyIdentifiedByAuthenticatedUsersCompanyId() {
        UUID authenticatedCompanyId = UUID.randomUUID();
        UserEntity admin = authenticatedUser(UserRole.ADMIN, authenticatedCompanyId);
        CreateCompanyRequest request = new CreateCompanyRequest("Updated Company", FORMATTED_CNPJ);
        when(companyGateway.editCompany(eq(authenticatedCompanyId), any(CreateCompanyRequest.class)))
                .thenReturn(new Company(authenticatedCompanyId, "Updated Company", NORMALIZED_CNPJ));

        useCase.editCompany(request, admin);

        verify(companyGateway).editCompany(
                eq(authenticatedCompanyId),
                eq(new CreateCompanyRequest("Updated Company", NORMALIZED_CNPJ)));
    }

    private UserEntity authenticatedUser(UserRole role, UUID companyId) {
        return new UserEntity(UUID.randomUUID(), "Authenticated User", "authenticated@monaly.com",
                "hashed", role, companyId);
    }
}
