package com.monaly.erpmonaly.application.usecases.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.domain.exceptions.MissingFieldsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CreateCompanyUseCase {
    private final CompanyGateway companyGateway;

    public CreateCompanyUseCase(CompanyGateway companyGateway){
        this.companyGateway = companyGateway;
    }
    public Company createCompany(CreateCompanyRequest createCompanyRequest) {
        Map<String, String> fields = new HashMap<>();
        if (createCompanyRequest.name() == null) {
            fields.put("name", "O campo name é obrigatório");
        }
        if (createCompanyRequest.cnpj() == null) {
            fields.put("cnpj", "O campo cnpj é obrigatório");
        }

        if (!fields.isEmpty()) {
            throw new MissingFieldsException(
                    "Existem campos obrigatórios não preenchidos",
                    fields
            );
        }
        String normalizedCnpj = normalizeCNPJ(createCompanyRequest.cnpj());
        Company companyExist = companyGateway.findByCnpj(normalizedCnpj);
        if (companyExist != null) {
            throw new DuplicationException(
                    "Company already exists"
            );
        }
        CreateCompanyRequest finalCreateCompany = new CreateCompanyRequest(createCompanyRequest.name(), normalizedCnpj);

        Company createdCompany = null;
        try {
            createdCompany = companyGateway.save(finalCreateCompany);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();
            if (message != null && (message.contains("companies_tax_id_key"))) {
                throw new DuplicationException("Já existe uma empresa cadastrada com este CNPJ.");
            }
            throw new RuntimeException(message);
        }

        return createdCompany;
    }

    private String normalizeCNPJ(String cnpj){
        return cnpj.replaceAll("\\D", "");
    }
}
