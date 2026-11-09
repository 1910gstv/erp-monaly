package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.OffsetDateTime;
import java.util.Optional;
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
        when(companyGateway.findById(COMPANY_ID)).thenReturn(Optional.of(company()));
    }

    @Test
    void companyAdminCanEditItsCompany() {
        UserEntity admin = authenticatedUser(UserRole.ADMIN, COMPANY_ID);
        CreateCompanyRequest request = new CreateCompanyRequest("Updated Company", FORMATTED_CNPJ);
        Company updated = new Company(COMPANY_ID, "Updated Company", NORMALIZED_CNPJ);
        when(companyGateway.editCompany(eq(COMPANY_ID), any(CreateCompanyRequest.class))).thenReturn(updated);

        Company result = useCase.editCompany(COMPANY_ID, request, admin);

        assertEquals(updated, result);
        verify(companyGateway).editCompany(
                COMPANY_ID,
                new CreateCompanyRequest("Updated Company", NORMALIZED_CNPJ));
    }

    @Test
    void nonAdminCannotEditCompany() {
        UserEntity user = authenticatedUser(UserRole.USER, COMPANY_ID);

        assertThrows(AccessDeniedException.class,
                () -> useCase.editCompany(COMPANY_ID,
                        new CreateCompanyRequest("Updated Company", FORMATTED_CNPJ), user));

        verify(companyGateway, never()).editCompany(any(), any());
    }

    @Test
    void adminCannotEditAnotherCompany() {
        UserEntity admin = authenticatedUser(UserRole.ADMIN, UUID.randomUUID());

        assertThrows(AccessDeniedException.class,
                () -> useCase.editCompany(COMPANY_ID,
                        new CreateCompanyRequest("Updated Company", FORMATTED_CNPJ), admin));

        verify(companyGateway, never()).editCompany(any(), any());
    }

    @Test
    void cannotEditUnknownCompany() {
        when(companyGateway.findById(COMPANY_ID)).thenReturn(Optional.empty());
        UserEntity admin = authenticatedUser(UserRole.ADMIN, COMPANY_ID);

        assertThrows(DuplicationException.class,
                () -> useCase.editCompany(COMPANY_ID,
                        new CreateCompanyRequest("Updated Company", FORMATTED_CNPJ), admin));

        verify(companyGateway, never()).editCompany(any(), any());
    }

    private CompanyEntity company() {
        OffsetDateTime now = OffsetDateTime.now();
        return new CompanyEntity(COMPANY_ID, "Mercado Monaly", NORMALIZED_CNPJ, now, now);
    }

    private UserEntity authenticatedUser(UserRole role, UUID companyId) {
        return new UserEntity(UUID.randomUUID(), "Authenticated User", "authenticated@monaly.com",
                "hashed", role, companyId);
    }
}
