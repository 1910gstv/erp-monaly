package com.monaly.erpmonaly.application.gateways.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyGateway {
    Company save(CreateCompanyRequest createCompanyRequest);
    List<Company> listAll();
    Company findByCnpj(String tax_id);
    Optional<CompanyEntity> findById(UUID company_id);
}
