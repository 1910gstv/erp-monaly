package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateCompanyUseCase {
    private final CompanyGateway companyGateway;

    public CreateCompanyUseCase(CompanyGateway companyGateway){
        this.companyGateway = companyGateway;
    }
    public Company createCompany(CreateCompanyRequest createCompanyRequest) {
        return companyGateway.createCompany(createCompanyRequest);
    }
}
