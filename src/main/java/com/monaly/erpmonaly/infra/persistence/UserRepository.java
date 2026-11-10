package com.monaly.erpmonaly.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
    List<UserEntity> findAllUsersByCompanyId(UUID company_id);

}
