package com.monaly.erpmonaly.api.controllers.users;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.application.usecases.companies.ListCompaniesUseCase;
import com.monaly.erpmonaly.application.usecases.users.CreateUserUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UsersController {
    private final CreateUserUseCase createUserUseCase;

    public UsersController(CreateUserUseCase createUserUseCase){
        this.createUserUseCase = createUserUseCase;
    };

    @PostMapping("/create")
    public CreateUserResponse create(@Valid @RequestBody  CreateUserRequest request){
        return createUserUseCase.create(request);
    };

}
