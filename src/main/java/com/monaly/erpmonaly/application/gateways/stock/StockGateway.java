package com.monaly.erpmonaly.application.gateways.stock;

import com.monaly.erpmonaly.application.DTOs.stock.CreateStockDTO;
import com.monaly.erpmonaly.domain.entities.stock.Stock;
import com.monaly.erpmonaly.infra.persistence.StockEntity;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface StockGateway {
    Stock create(CreateStockDTO request);
    Optional<Stock> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<Stock> findAllByCompanyId(UUID companyId);
    Stock update(StockEntity request);
}
