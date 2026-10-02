package com.monaly.erpmonaly.domain.entities.companies;

import java.util.UUID;

public record Company(UUID id, String name, String cnpj) {
    public Company(UUID id, String name, String cnpj){
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
    }
}
