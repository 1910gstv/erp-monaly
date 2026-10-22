package com.monaly.erpmonaly.api.controllers;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.usecases.auth.RegisterCompanyAdminUseCase;
import com.monaly.erpmonaly.domain.DTO.AuthenticationDTO;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import com.monaly.erpmonaly.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    private final RegisterCompanyAdminUseCase registerCompanyAdminUseCase;

    public AuthController(RegisterCompanyAdminUseCase registerCompanyAdminUseCase) {
        this.registerCompanyAdminUseCase = registerCompanyAdminUseCase;
    }

    @PostMapping("/login")
        public ResponseEntity login(@RequestBody AuthenticationDTO data){
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((UserEntity) auth.getPrincipal());
            return ResponseEntity.ok(token);
        }

        @PostMapping("/register")
        public CreateUserResponse register(@Valid @RequestBody CreateUserRequest request){
            return registerCompanyAdminUseCase.register(request);
        };
}
