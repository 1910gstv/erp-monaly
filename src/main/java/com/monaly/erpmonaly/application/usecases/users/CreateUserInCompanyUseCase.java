package com.monaly.erpmonaly.application.usecases.users;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserInCompanyRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CreateUserInCompanyUseCase {
    private final UserGateway userGateway;
    private final CompanyGateway companyGateway;
    private final PasswordEncoder passwordEncoder;
    private final CreateCompanyUseCase createCompanyUseCase;

    public CreateUserInCompanyUseCase(UserGateway userGateway, CompanyGateway companyGateway, PasswordEncoder passwordEncoder, CreateCompanyUseCase createCompanyUseCase) {
        this.userGateway = userGateway;
        this.companyGateway = companyGateway;
        this.passwordEncoder = passwordEncoder;
        this.createCompanyUseCase = createCompanyUseCase;
    }

    public CreateUserResponse createUser(CreateUserInCompanyRequest request, UUID companyId, UserEntity userEntity) throws Exception {

        Optional<CompanyEntity> findCompanyById = companyGateway.findById(companyId);

        if(findCompanyById.isEmpty()){
            throw new DuplicationException("Essa empresa não existe");
        }

        if(userEntity.getCompanyId() != companyId || userEntity.getRole() != UserRole.ADMIN){
            throw new Exception("Usuário não pertence a empresa informada e/ou usuário não é admin");
        }

        System.out.println(userEntity.getCompanyId());
        System.out.println(userEntity.getRole());
        boolean emailExist = userGateway.existsByEmail(request.email());
        if(emailExist){
            throw new DuplicationException("O email informado já existe, tente outro");
        }

        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        if(!request.password().matches(pattern)){
            throw new IllegalArgumentException("A senha deve conter no mínimo 8 caracteres, uma letra minúscula, uma maiúscula, um caractere especial e um número");
        };

        String hashedPassword = passwordEncoder.encode(request.password());

        CreateUserRequest finalUser = new CreateUserRequest(
                request.username(),
                request.email(),
                hashedPassword,
                request.role(),
                findCompanyById.get().getCnpj(),
                findCompanyById.get().getName(),
                companyId
        );

        return userGateway.save(finalUser);

    }
}
