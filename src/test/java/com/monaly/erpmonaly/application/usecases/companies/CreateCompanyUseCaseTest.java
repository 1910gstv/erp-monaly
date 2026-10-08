package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.exceptions.CompanyAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateCompanyUseCaseTest {

    private CompanyGateway companyGateway;
    private CreateCompanyUseCase useCase;

    @BeforeEach
    void setUp() {
        companyGateway = mock(CompanyGateway.class);
        useCase = new CreateCompanyUseCase(companyGateway);
    }

    @Test
    void createsCompanyWhenCnpjDoesNotExist() {
        CreateCompanyRequest request = new CreateCompanyRequest("Mercado Monaly", "12345678000199");
        Company expected = new Company(UUID.randomUUID(), request.name(), request.cnpj());
        when(companyGateway.findByCnpj(request.cnpj())).thenReturn(null);
        when(companyGateway.save(request)).thenReturn(expected);

        Company result = useCase.createCompany(request);

        assertEquals(expected, result);
        verify(companyGateway).save(request);
    }

    @Test
    void rejectsCompanyWhenCnpjAlreadyExists() {
        CreateCompanyRequest request = new CreateCompanyRequest("Mercado Monaly", "12345678000199");
        when(companyGateway.findByCnpj(request.cnpj()))
                .thenReturn(new Company(UUID.randomUUID(), request.name(), request.cnpj()));

        assertThrows(CompanyAlreadyExistsException.class, () -> useCase.createCompany(request));

        verify(companyGateway, never()).save(request);
    }
}
