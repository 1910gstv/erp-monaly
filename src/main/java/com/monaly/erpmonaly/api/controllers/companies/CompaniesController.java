package com.monaly.erpmonaly.api.controllers.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("companies")
public class CompaniesController {
    private final CreateCompanyUseCase createCompanyUseCase;

    public CompaniesController(CreateCompanyUseCase createCompanyUseCase){
        this.createCompanyUseCase = createCompanyUseCase;
    }

    @PostMapping("/create")
    public Company create(@Valid @RequestBody CreateCompanyRequest request){
        Company company = createCompanyUseCase.createCompany(request);
        return company;
    }
}
