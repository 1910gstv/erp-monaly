package com.monaly.erpmonaly.api.controllers.products;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.usecases.products.CreateProductUseCase;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductsController {
    private final CreateProductUseCase createProductUseCase;

    public ProductsController(CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
    }
    @PostMapping("/create")
    public Product create(@Valid @RequestBody CreateProductRequest request, @AuthenticationPrincipal UserEntity userEntity) throws Exception{
        return createProductUseCase.create(request, userEntity);
    }
}
