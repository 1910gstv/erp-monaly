package com.monaly.erpmonaly.api.controllers.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserInCompanyRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.application.usecases.companies.EditCompanyUseCase;
import com.monaly.erpmonaly.application.usecases.companies.ListCompaniesUseCase;
import com.monaly.erpmonaly.application.usecases.users.CreateUserInCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("companies")
public class CompaniesController {
    private final CreateCompanyUseCase createCompanyUseCase;
    private final ListCompaniesUseCase listCompaniesUseCase;
    private final CreateUserInCompanyUseCase createUserInCompanyUseCase;
    private final EditCompanyUseCase editCompanyUseCase;

    public CompaniesController(CreateCompanyUseCase createCompanyUseCase,
                               ListCompaniesUseCase listCompaniesUseCase,
                               CreateUserInCompanyUseCase createUserInCompanyUseCase, EditCompanyUseCase editCompanyUseCase
    ){
        this.createCompanyUseCase = createCompanyUseCase;
        this.listCompaniesUseCase = listCompaniesUseCase;
        this.createUserInCompanyUseCase = createUserInCompanyUseCase;
        this.editCompanyUseCase = editCompanyUseCase;
    }

    @PostMapping("/create")
    public Company create(@Valid @RequestBody CreateCompanyRequest request) throws Exception {
    //public Company create(CreateCompanyRequest request){
        return createCompanyUseCase.createCompany(request);
    }

    @PostMapping("/{companyId}/users/create")
    public CreateUserResponse createUser(@PathVariable UUID companyId,
                                         @Valid @RequestBody CreateUserInCompanyRequest request,
                                         @AuthenticationPrincipal UserEntity userEntity
                                         ) throws Exception {
        return createUserInCompanyUseCase.createUser(request, companyId, userEntity);
    }

    @GetMapping("/list")
    public List<Company> listAll(){
        return listCompaniesUseCase.listAll();
    }

    @PutMapping("/{id}/edit")
    public Company edit(@PathVariable UUID id, @Valid @RequestBody CreateCompanyRequest request, @AuthenticationPrincipal UserEntity userEntity) throws Exception {
        return editCompanyUseCase.editCompany(id, request, userEntity);
    }
}
