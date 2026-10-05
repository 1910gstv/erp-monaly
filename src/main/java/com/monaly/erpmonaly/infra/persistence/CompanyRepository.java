package com.monaly.erpmonaly.infra.persistence;

import com.monaly.erpmonaly.domain.entities.companies.Company;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CompanyRepository extends CrudRepository<CompanyEntity, UUID> {
}
