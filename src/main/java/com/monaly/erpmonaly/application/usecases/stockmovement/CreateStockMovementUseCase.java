package com.monaly.erpmonaly.application.usecases.stockmovement;

import com.monaly.erpmonaly.application.DTOs.stock.CreateStockDTO;
import com.monaly.erpmonaly.application.DTOs.stock.CreateStockMovementDTO;
import com.monaly.erpmonaly.application.DTOs.stock.CreateStockMovementRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.application.gateways.stock.StockGateway;
import com.monaly.erpmonaly.application.gateways.stock.StockMovementGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.entities.stock.Stock;
import com.monaly.erpmonaly.domain.entities.stock.StockMovement;
import com.monaly.erpmonaly.infra.persistence.StockEntity;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreateStockMovementUseCase {
    private final StockMovementGateway stockMovementGateway;
    private final StockGateway stockGateway;
    private final ProductGateway productGateway;

    public CreateStockMovementUseCase(StockMovementGateway stockMovementGateway, StockGateway stockGateway, ProductGateway productGateway) {
        this.stockMovementGateway = stockMovementGateway;
        this.stockGateway = stockGateway;
        this.productGateway = productGateway;
    }

    @Transactional
    public StockMovement create(CreateStockMovementRequest request, UserEntity userEntity) throws Exception {
        UUID companyId = userEntity.getCompanyId();
        UUID userId = userEntity.getId();

        Product findedProduct = productGateway.findByIdAndCompany(request.productId(), companyId);
        if(findedProduct == null){
            throw new EntityNotFoundException("Esse produto não existe nessa empresa");
        }

        CreateStockMovementDTO dto = new CreateStockMovementDTO(
                companyId,
                request.productId(),
                userId,
                request.type(),
                request.quantity(),
                request.reason(),
                OffsetDateTime.now()
        );

        StockMovement createdMovement = null;
        try {
            Optional<Stock> existingStock = stockGateway.findByIdAndCompanyId(request.productId(), companyId);
            if (existingStock.isPresent()) {
                Stock findedStock = existingStock.get();
                findedStock.calculateNewStock(request.type(), request.quantity());
                StockEntity entity = new StockEntity(
                        findedStock.getId(),
                        findedStock.getCompanyId(),
                        findedStock.getProductId(),
                        findedStock.getQuantity(),
                        findedStock.getCreatedAt(),
                        findedStock.getUpdatedAt(),
                        findedStock.getDeletedAt()
                );
                stockGateway.update(entity);
            } else {
                // Se não: criar linha de estoque
                CreateStockDTO newStock = new CreateStockDTO(
                        companyId,
                        request.productId(),
                        request.quantity(),
                        OffsetDateTime.now(),
                        OffsetDateTime.now(),
                        null
                );
                stockGateway.create(newStock);
            }
            createdMovement = stockMovementGateway.create(dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return createdMovement;
    }
}
