package com.monaly.erpmonaly.application.usecases.stockmovement;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.DTOs.stock.CreateStockMovementDTO;
import com.monaly.erpmonaly.application.DTOs.stock.CreateStockMovementRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.application.gateways.stock.StockMovementGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.entities.stock.StockMovement;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CreateStockMovementUseCase {
    private final StockMovementGateway stockMovementGateway;

    public CreateStockMovementUseCase(StockMovementGateway stockMovementGateway) {
        this.stockMovementGateway = stockMovementGateway;
    }

    public StockMovement create(CreateStockMovementRequest request, UserEntity userEntity){
        UUID companyId = userEntity.getCompanyId();
        UUID userId = userEntity.getId();

        CreateStockMovementDTO dto = new CreateStockMovementDTO(
                companyId,
                request.productId(),
                userId,
                request.type(),
                request.quantity(),
                request.reason(),
                OffsetDateTime.now()
        );

        // TODO:
        // Verificar se produto existe
        // Verificar se estoque com produto já existe
        // Se sim: Atualizar estoque
        // Se não: criar linha de estoque
        StockMovement createdMovement = null;
        try {
            createdMovement = stockMovementGateway.create(dto);
        } catch(DataIntegrityViolationException e){
            String message = e.getMostSpecificCause().getMessage();
            // if (message != null && (message.contains("uq_products_company_sku"))) {
              //  throw new DuplicationException("Já existe produtos com essa SKU cadastrado nessa empresa");
            // }
            throw e;
        }

        return createdMovement;
    }
}
