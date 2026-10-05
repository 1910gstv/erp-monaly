package com.monaly.erpmonaly.infra.gateways.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.CompanyRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class CompanyRepositoryGateway implements CompanyGateway {
    private final CompanyRepository companyRepository;

    public CompanyRepositoryGateway(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company save(CreateCompanyRequest createCompanyRequest) {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        Company company = new Company(
                id, createCompanyRequest.name(), createCompanyRequest.cnpj()
        );

        CompanyEntity entity = new CompanyEntity(
                company.getId(),
                company.getName(),
                company.getCnpj(),
                now,
                now
        );

        CompanyEntity savedEntity = companyRepository.save(entity);
        return new Company(
                savedEntity.getId(),
                savedEntity.getName(),
                savedEntity.getCnpj()
        );
    }

    @Override
    public List<Company> listAll() {
        List<CompanyEntity> companies = companyRepository.findAll();
        return companies.stream().map(companyEntity -> new Company(
                companyEntity.getId(),
                companyEntity.getName(),
                companyEntity.getCnpj()
        )).toList();
    }
}
