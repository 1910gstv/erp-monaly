package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.CredentialException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListCompaniesUseCaseTest {

    private CompanyGateway companyGateway;
    private ListCompaniesUseCase useCase;

    @BeforeEach
    void setUp() {
        companyGateway = mock(CompanyGateway.class);
        useCase = new ListCompaniesUseCase(companyGateway);
    }

    @Test
    void masterCanListCompaniesAcrossTenants() throws CredentialException {
        UserEntity master = authenticatedUser(UserRole.MASTER);
        List<Company> companies = List.of(
                new Company(UUID.randomUUID(), "Company One", "11111111111111"),
                new Company(UUID.randomUUID(), "Company Two", "22222222222222")
        );
        when(companyGateway.listAll()).thenReturn(companies);

        assertEquals(companies, useCase.listAll(master));

        verify(companyGateway).listAll();
    }

    @Test
    void adminCannotListCompaniesAcrossTenants() {
        assertThrows(CredentialException.class,
                () -> useCase.listAll(authenticatedUser(UserRole.ADMIN)));

        verify(companyGateway, never()).listAll();
    }

    @Test
    void regularUserCannotListCompaniesAcrossTenants() {
        assertThrows(CredentialException.class,
                () -> useCase.listAll(authenticatedUser(UserRole.USER)));

        verify(companyGateway, never()).listAll();
    }

    private UserEntity authenticatedUser(UserRole role) {
        return new UserEntity(UUID.randomUUID(), role.name(), role.name().toLowerCase() + "@monaly.com",
                "hashed", role, UUID.randomUUID());
    }
}
