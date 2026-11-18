package com.monaly.erpmonaly.infra.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    @Query("""
            select p
            from ProductEntity p
            where p.companyId = :companyId
              and p.deletedAt is null
              and (:id is null or p.id = :id)
              and (:sku is null or p.sku = :sku)
              and (:name is null or lower(p.name) like lower(concat('%', cast(:name as String), '%')))
              and (:state is null or p.state = :state)
            """)
    Page<ProductEntity> findAllProductsByCompanyId(
            @Param("companyId") UUID company_id,
            @Param("name") String name,
            @Param("sku") String sku,
            @Param("id") UUID id,
            @Param("state") String state,
            Pageable pageable
    );
}
