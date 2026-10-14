package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateCompanyUseCaseTest {

    private static final String FORMATTED_CNPJ = "11.222.333/0001-81";
    private static final String NORMALIZED_CNPJ = "11222333000181";

    private CompanyGateway companyGateway;
    private CreateCompanyUseCase useCase;

    @BeforeEach
    void setUp() {
        companyGateway = mock(CompanyGateway.class);
        useCase = new CreateCompanyUseCase(companyGateway);
    }

    @Test
    void createsCompanyWithNormalizedCnpjWhenCnpjDoesNotExist() {
        CreateCompanyRequest request = new CreateCompanyRequest("Mercado Monaly", FORMATTED_CNPJ);
        CreateCompanyRequest normalizedRequest = new CreateCompanyRequest("Mercado Monaly", NORMALIZED_CNPJ);
        Company expected = new Company(UUID.randomUUID(), normalizedRequest.name(), normalizedRequest.cnpj());
        when(companyGateway.findByCnpj(NORMALIZED_CNPJ)).thenReturn(null);
        when(companyGateway.save(normalizedRequest)).thenReturn(expected);

        Company result = useCase.createCompany(request);

        assertEquals(expected, result);
        verify(companyGateway).findByCnpj(NORMALIZED_CNPJ);
        verify(companyGateway).save(normalizedRequest);
    }

    @Test
    void rejectsCompanyWhenCnpjAlreadyExists() {
        CreateCompanyRequest request = new CreateCompanyRequest("Mercado Monaly", FORMATTED_CNPJ);
        when(companyGateway.findByCnpj(NORMALIZED_CNPJ))
                .thenReturn(new Company(UUID.randomUUID(), request.name(), NORMALIZED_CNPJ));

        assertThrows(DuplicationException.class, () -> useCase.createCompany(request));

        verify(companyGateway, never()).save(any());
    }
}
