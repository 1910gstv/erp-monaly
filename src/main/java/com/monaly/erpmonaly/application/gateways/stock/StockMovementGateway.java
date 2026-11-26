package com.monaly.erpmonaly.application.gateways.stock;

import com.monaly.erpmonaly.application.DTOs.stock.CreateStockMovementDTO;
import com.monaly.erpmonaly.application.DTOs.stock.CreateStockMovementRequest;
import com.monaly.erpmonaly.domain.entities.stock.StockMovement;

public interface StockMovementGateway {
    StockMovement create(CreateStockMovementDTO request);
}
