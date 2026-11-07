package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EditCompanyUseCase {
    private final CompanyGateway companyGateway;

    public EditCompanyUseCase(CompanyGateway companyGateway) {
        this.companyGateway = companyGateway;
    }

    public Company editCompany(UUID id, CreateCompanyRequest request, UserEntity userEntity){

        return companyGateway.editCompany(id, request);
    }
}
