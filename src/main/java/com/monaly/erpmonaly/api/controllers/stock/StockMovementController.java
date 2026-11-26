package com.monaly.erpmonaly.api.controllers.stock;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.DTOs.stock.CreateStockMovementRequest;
import com.monaly.erpmonaly.application.usecases.stockmovement.CreateStockMovementUseCase;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.entities.stock.StockMovement;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("stock-movements")
public class StockMovementController {

    private final CreateStockMovementUseCase createStockMovementUseCase;

    public StockMovementController(CreateStockMovementUseCase createStockMovementUseCase) {
        this.createStockMovementUseCase = createStockMovementUseCase;
    }

    @PostMapping("/create")
    public StockMovement create(@Valid @RequestBody CreateStockMovementRequest request, @AuthenticationPrincipal UserEntity userEntity) throws Exception{
        return createStockMovementUseCase.create(request, userEntity);
    }
}
