package com.monaly.erpmonaly.application.gateways.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.domain.entities.companies.Company;

public interface CompanyGateway {
    Company createCompany(CreateCompanyRequest createCompanyRequest);
}
