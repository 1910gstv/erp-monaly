package com.monaly.erpmonaly.application.gateways.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.domain.entities.companies.Company;

import java.util.List;

public interface CompanyGateway {
    Company save(CreateCompanyRequest createCompanyRequest);
    List<Company> listAll();
}
