package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.domain.exceptions.MissingFieldsException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CreateCompanyUseCase {
    private final CompanyGateway companyGateway;

    public CreateCompanyUseCase(CompanyGateway companyGateway){
        this.companyGateway = companyGateway;
    }
    public Company createCompany(CreateCompanyRequest createCompanyRequest) {
        Map<String, String> fields = new HashMap<>();
        if(createCompanyRequest.name() == null){
            fields.put("name", "O campo name é obrigatório");
        }
        if(createCompanyRequest.cnpj() == null){
            fields.put("cnpj", "O campo cnpj é obrigatório");
        }

        if (!fields.isEmpty()) {
            throw new MissingFieldsException(
                    "Existem campos obrigatórios não preenchidos",
                    fields
            );
        }
        Company companyExist = companyGateway.findByCnpj(createCompanyRequest.cnpj());

        if(companyExist != null){
            throw new DuplicationException(
                    "Company already exists"
            );
        }

        return companyGateway.save(createCompanyRequest);
    }
}
