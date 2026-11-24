package com.monaly.erpmonaly.api.controllers.products;

import com.monaly.erpmonaly.application.DTOs.EditProductRequest;
import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.usecases.products.CreateProductUseCase;
import com.monaly.erpmonaly.application.usecases.products.EditProductUseCase;
import com.monaly.erpmonaly.application.usecases.products.ListProductsByCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("products")
public class ProductsController {
    private final CreateProductUseCase createProductUseCase;
    private final ListProductsByCompanyUseCase listProductsByCompanyUseCase;
    private final EditProductUseCase editProductUseCase;

    public ProductsController(CreateProductUseCase createProductUseCase, ListProductsByCompanyUseCase listProductsByCompanyUseCase, EditProductUseCase editProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsByCompanyUseCase = listProductsByCompanyUseCase;
        this.editProductUseCase = editProductUseCase;
    }
    @PostMapping("/create")
    public Product create(@Valid @RequestBody CreateProductRequest request, @AuthenticationPrincipal UserEntity userEntity) throws Exception{
        return createProductUseCase.create(request, userEntity);
    }

    @GetMapping("/all")
    public Page<Product> findAll(@AuthenticationPrincipal UserEntity userEntity,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String sku,
                                 @RequestParam(required = false) UUID id,
                                 @RequestParam(required = false) String state
    ) throws Exception {

        return listProductsByCompanyUseCase.listAllByCompany(userEntity, page, size, name, sku, id, state);
    }

    @PutMapping("/edit/me/{productId}")
    public Product edit (
            @AuthenticationPrincipal UserEntity userEntity,
            @Valid @RequestBody EditProductRequest data,
            @PathVariable UUID productId
    )throws Exception{
        return editProductUseCase.edit(userEntity, data, productId);
    }

    @PutMapping("/edit/deactivate/{productId}")
    public Product deactivate (
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable UUID productId
    )throws Exception{
        return editProductUseCase.deactivate(userEntity, productId);
    }

    @PutMapping("/edit/reactivate/{productId}")
    public Product reactivate (
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable UUID productId
    )throws Exception{
        return editProductUseCase.reactivate(userEntity, productId);
    }
}
