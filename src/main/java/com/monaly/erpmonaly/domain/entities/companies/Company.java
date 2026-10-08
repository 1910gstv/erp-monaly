package com.monaly.erpmonaly.domain.entities.companies;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record Company(
        UUID id,

        @NotBlank(message = "o campo name é obrigatório")
        String name,

        @NotBlank(message = "o campo cnpj é obrigatório")
        String cnpj
) {
    public Company(UUID id, String name, String cnpj){
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCnpj() {
        return cnpj;
    }
}
