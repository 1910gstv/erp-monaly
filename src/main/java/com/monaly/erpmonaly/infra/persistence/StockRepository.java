package com.monaly.erpmonaly.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<StockEntity, UUID> {
    Optional<StockEntity> findByProductIdAndCompanyId(UUID productId, UUID companyId);
}
