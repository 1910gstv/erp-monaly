package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCompaniesUseCase {

    private final CompanyGateway companyGateway;

    public ListCompaniesUseCase(CompanyGateway companyGateway) {
        this.companyGateway = companyGateway;
    }

    public List<Company> listAll(){
        return companyGateway.listAll();
    }
}
