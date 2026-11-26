package com.monaly.erpmonaly.infra.gateways.stock;

import com.monaly.erpmonaly.application.DTOs.stock.CreateStockDTO;
import com.monaly.erpmonaly.application.gateways.stock.StockGateway;
import com.monaly.erpmonaly.domain.entities.stock.Stock;
import com.monaly.erpmonaly.infra.persistence.StockEntity;
import com.monaly.erpmonaly.infra.persistence.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StockRepositoryGateway implements StockGateway {
    private final StockRepository stockRepository;

    public StockRepositoryGateway(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public Stock create(CreateStockDTO request) {
        UUID id = UUID.randomUUID();

        StockEntity entity = new StockEntity(
                id,
                request.companyId(),
                request.productId(),
                request.quantity(),
                request.createdAt(),
                request.updatedAt(),
                request.deletedAt()
        );
        StockEntity saved = stockRepository.save(entity);

        return new Stock(
                saved.getId(),
                saved.getCompanyId(),
                saved.getProductId(),
                saved.getQuantity(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getDeletedAt()
        );
    }

    @Override
    public Optional<Stock> findByIdAndCompanyId(UUID productId, UUID companyId) {
        return stockRepository.findByProductIdAndCompanyId(productId, companyId)
                .map(entity -> new Stock(
                        entity.getId(),
                        entity.getCompanyId(),
                        entity.getProductId(),
                        entity.getQuantity(),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt(),
                        entity.getDeletedAt()
                ));
    }

    @Override
    public Page<Stock> findAllByCompanyId(UUID companyId) {
        return null;
    }

    @Override
    public Stock update(StockEntity request) {
        StockEntity saved = stockRepository.save(request);

        return new Stock(
                saved.getId(),
                saved.getCompanyId(),
                saved.getProductId(),
                saved.getQuantity(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getDeletedAt()
        );
    }
}
