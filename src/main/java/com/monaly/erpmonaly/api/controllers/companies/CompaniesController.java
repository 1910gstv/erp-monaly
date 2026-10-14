package com.monaly.erpmonaly.api.controllers.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.application.usecases.companies.ListCompaniesUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("companies")
public class CompaniesController {
    private final CreateCompanyUseCase createCompanyUseCase;
    private final ListCompaniesUseCase listCompaniesUseCase;

    public CompaniesController(CreateCompanyUseCase createCompanyUseCase, ListCompaniesUseCase listCompaniesUseCase){
        this.createCompanyUseCase = createCompanyUseCase;
        this.listCompaniesUseCase = listCompaniesUseCase;
    }

    @PostMapping("/create")
    public Company create(@Valid @RequestBody CreateCompanyRequest request) throws Exception {
    //public Company create(CreateCompanyRequest request){
        return createCompanyUseCase.createCompany(request);
    }

    @GetMapping("/list")
    public List<Company> listAll(){
        return listCompaniesUseCase.listAll();
    }
}
