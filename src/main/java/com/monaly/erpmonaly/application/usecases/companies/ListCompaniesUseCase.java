package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialException;
import java.util.List;
import java.util.UUID;

@Service
public class ListCompaniesUseCase {

    private final CompanyGateway companyGateway;

    public ListCompaniesUseCase(CompanyGateway companyGateway) {
        this.companyGateway = companyGateway;
    }

    public List<Company> listAll(UserEntity userEntity) throws CredentialException {
        UserRole userRole = userEntity.getRole();

        // if(userRole != UserRole.MASTER) {
        if(userRole != UserRole.ADMIN) {
            throw new CredentialException("Esse cargo não permite acesso");
        }
        return companyGateway.listAll();
    }
}
