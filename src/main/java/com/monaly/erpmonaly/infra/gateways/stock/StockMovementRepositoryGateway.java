package com.monaly.erpmonaly.infra.gateways.stock;

import com.monaly.erpmonaly.application.DTOs.stock.CreateStockMovementDTO;
import com.monaly.erpmonaly.application.gateways.stock.StockMovementGateway;
import com.monaly.erpmonaly.domain.entities.stock.StockMovement;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;
import com.monaly.erpmonaly.infra.persistence.ProductRepository;
import com.monaly.erpmonaly.infra.persistence.StockMovementEntity;
import com.monaly.erpmonaly.infra.persistence.StockMovementRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public class StockMovementRepositoryGateway implements StockMovementGateway {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementRepositoryGateway(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    public StockMovement create(CreateStockMovementDTO request) {
        UUID id = UUID.randomUUID();

        StockMovementEntity movement = new StockMovementEntity(
                id,
                request.companyId(),
                request.productId(),
                request.userId(),
                request.type(),
                request.quantity(),
                request.reason(),
                request.createdAt()
        );
        StockMovementEntity movementSaved = stockMovementRepository.save(movement);

        return new StockMovement(
            movementSaved.getId(),
            movementSaved.getCompanyId(),
            movementSaved.getProductId(),
            movementSaved.getUserId(),
            movementSaved.getType(),
            movementSaved.getQuantity(),
            movementSaved.getReason(),
            movementSaved.getCreatedAt()
        );
    }
}
