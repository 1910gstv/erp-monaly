package com.monaly.erpmonaly.application.usecases.auth;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterCompanyAdminUseCase {
    private final UserGateway userGateway;
    private final CompanyGateway companyGateway;
    private final PasswordEncoder passwordEncoder;
    private final CreateCompanyUseCase createCompanyUseCase;

    public RegisterCompanyAdminUseCase(UserGateway userGateway, CompanyGateway companyGateway, PasswordEncoder passwordEncoder, CreateCompanyUseCase createCompanyUseCase){
        this.userGateway = userGateway;
        this.companyGateway = companyGateway;
        this.passwordEncoder = passwordEncoder;
        this.createCompanyUseCase = createCompanyUseCase;
    }

    @Transactional
    public CreateUserResponse register(CreateUserRequest request){
        boolean emailExist = userGateway.existsByEmail(request.email());
        if(emailExist){
            throw new DuplicationException("O email informado já existe, tente outro");
        }

        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        if(!request.password().matches(pattern)){
            throw new IllegalArgumentException("A senha deve conter no mínimo 8 caracteres, uma letra minúscula, uma maiúscula, um caractere especial e um número");
        };

        String hashedPassword = passwordEncoder.encode(request.password());

        String normalizedCnpj = createCompanyUseCase.normalizeCNPJ(request.cnpj());
        Company findCompanyByCnpj = companyGateway.findByCnpj(normalizedCnpj);

        if(findCompanyByCnpj != null){
            throw new DuplicationException("Já existe uma empresa cadastrada com este CNPJ.");
        }

        CreateCompanyRequest companyRequest = new CreateCompanyRequest(request.companyName(), request.cnpj());
        Company createdCompany = createCompanyUseCase.createCompany(companyRequest);

        CreateUserRequest finalUser = new CreateUserRequest(
                request.username(),
                request.email(),
                hashedPassword,
                UserRole.ADMIN,
                request.cnpj(),
                request.companyName(),
                createdCompany.getId()
        );

      return userGateway.save(finalUser);
    };

}
