package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EditCompanyUseCase {
    private final CompanyGateway companyGateway;

    public EditCompanyUseCase(CompanyGateway companyGateway) {
        this.companyGateway = companyGateway;
    }

    public Company editCompany(CreateCompanyRequest request, UserEntity userEntity) {
        UUID companyId = userEntity.getCompanyId();
        if(!userEntity.getRole().equals(UserRole.ADMIN)){
            throw new AccessDeniedException("Usuário não pertence à empresa informada ou não é administrador");
        }
        String normalizedCnpj = normalizeCNPJ(request.cnpj());

        CreateCompanyRequest newRequest = new CreateCompanyRequest(
                request.name(),
                normalizedCnpj
        );
        return companyGateway.editCompany(companyId, newRequest);
    }

    public String normalizeCNPJ(String cnpj){
        return cnpj.replaceAll("\\D", "");
    }
}
