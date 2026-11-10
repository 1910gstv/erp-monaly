package com.monaly.erpmonaly.application.usecases.users;

import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.domain.entities.users.User;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUsersByCompanyUseCase {
    private final CompanyGateway companyGateway;
    private final UserGateway userGateway;

    public ListUsersByCompanyUseCase(CompanyGateway companyGateway, UserGateway userGateway) {
        this.companyGateway = companyGateway;
        this.userGateway = userGateway;
    }

    public List<User> listAllUsersByCompany(UserEntity userEntity){
        return userGateway.findAll(userEntity.getCompanyId());
    }
}
