package com.monaly.erpmonaly.api.controllers.users;

import com.monaly.erpmonaly.application.usecases.users.ListUsersByCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.entities.users.User;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
public class UsersController {
    private final ListUsersByCompanyUseCase listUsersByCompanyUseCase;

    public UsersController(ListUsersByCompanyUseCase listUsersByCompanyUseCase) {
        this.listUsersByCompanyUseCase = listUsersByCompanyUseCase;
    }

    @GetMapping("/list")
    public List<User> listAll(@AuthenticationPrincipal UserEntity userEntity) throws Exception {
        return listUsersByCompanyUseCase.listAllUsersByCompany(userEntity);
    }
}
