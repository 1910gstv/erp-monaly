package com.monaly.erpmonaly.infra.gateways.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class CompanyRepositoryGateway implements CompanyGateway {

    @Override
    public Company createCompany(CreateCompanyRequest createCompanyRequest) {
        return new Company(UUID.randomUUID(),"Repository", "1726284214");
    }
}
