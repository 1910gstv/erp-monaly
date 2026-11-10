package com.monaly.erpmonaly.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<CompanyEntity, UUID> {
    CompanyEntity findByCnpj(String tax_id);
    List<CompanyEntity> findAllByCompanyId(UUID company_id);
}
