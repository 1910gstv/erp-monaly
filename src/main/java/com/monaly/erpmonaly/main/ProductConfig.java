package com.monaly.erpmonaly.main;

import com.monaly.erpmonaly.application.gateways.products.ProductEntityMapper;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.infra.gateways.products.ProductRepositoryGateway;
import com.monaly.erpmonaly.infra.persistence.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfig {
    @Bean
    ProductGateway productGateway(ProductRepository productRepository, ProductEntityMapper productEntityMapper){
        return new ProductRepositoryGateway(productRepository,productEntityMapper);
    }

    @Bean
    ProductEntityMapper productEntityMapper() {return new ProductEntityMapper();}
}
