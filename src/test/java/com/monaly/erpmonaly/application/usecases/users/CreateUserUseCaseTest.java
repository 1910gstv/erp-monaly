package com.monaly.erpmonaly.application.usecases.users;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateUserUseCaseTest {

    private static final String FORMATTED_CNPJ = "11.222.333/0001-81";
    private static final String NORMALIZED_CNPJ = "11222333000181";
    private static final String RAW_PASSWORD = "StrongPass1@";
    private static final String HASHED_PASSWORD = "$2a$hashed-password";
    private static final UUID COMPANY_ID = UUID.randomUUID();

    private UserGateway userGateway;
    private CompanyGateway companyGateway;
    private PasswordEncoder passwordEncoder;
    private CreateCompanyUseCase createCompanyUseCase;
    private CreateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userGateway = mock(UserGateway.class);
        companyGateway = mock(CompanyGateway.class);
        passwordEncoder = mock(PasswordEncoder.class);
        createCompanyUseCase = mock(CreateCompanyUseCase.class);
        useCase = new CreateUserUseCase(
                userGateway,
                companyGateway,
                passwordEncoder,
                createCompanyUseCase
        );
    }

    @Test
    void createsCompanyAndAdminWithHashedPasswordAndSafeResponse() {
        CreateUserRequest request = request(UserRole.USER);
        CreateCompanyRequest expectedCompanyRequest =
                new CreateCompanyRequest(request.companyName(), request.cnpj());
        CreateUserResponse expectedResponse =
                new CreateUserResponse(request.username(), request.email(), UserRole.ADMIN);

        when(userGateway.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(createCompanyUseCase.normalizeCNPJ(FORMATTED_CNPJ)).thenReturn(NORMALIZED_CNPJ);
        when(companyGateway.findByCnpj(NORMALIZED_CNPJ)).thenReturn(null);
        when(createCompanyUseCase.createCompany(expectedCompanyRequest))
                .thenReturn(new Company(COMPANY_ID, request.companyName(), NORMALIZED_CNPJ));
        when(userGateway.save(any(CreateUserRequest.class))).thenReturn(expectedResponse);

        CreateUserResponse response = useCase.create(request);

        assertEquals(expectedResponse, response);
        assertEquals(3, response.getClass().getRecordComponents().length);
        assertTrue(java.util.Arrays.stream(response.getClass().getRecordComponents())
                .noneMatch(component -> component.getName().toLowerCase().contains("password")
                        || component.getName().toLowerCase().contains("hash")));

        var order = inOrder(userGateway, passwordEncoder, createCompanyUseCase, companyGateway);
        order.verify(userGateway).existsByEmail(request.email());
        order.verify(passwordEncoder).encode(RAW_PASSWORD);
        order.verify(createCompanyUseCase).normalizeCNPJ(FORMATTED_CNPJ);
        order.verify(companyGateway).findByCnpj(NORMALIZED_CNPJ);
        order.verify(createCompanyUseCase).createCompany(expectedCompanyRequest);

        var savedUser = org.mockito.ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(userGateway).save(savedUser.capture());
        assertEquals(request.username(), savedUser.getValue().username());
        assertEquals(request.email(), savedUser.getValue().email());
        assertEquals(HASHED_PASSWORD, savedUser.getValue().password());
        assertEquals(UserRole.ADMIN, savedUser.getValue().role());
        assertEquals(FORMATTED_CNPJ, savedUser.getValue().cnpj());
        assertEquals(request.companyName(), savedUser.getValue().companyName());
        assertEquals(COMPANY_ID, savedUser.getValue().companyId());
    }

    @Test
    void rejectsEmailAlreadyInUseWithoutCreatingCompanyOrUser() {
        CreateUserRequest request = request(UserRole.USER);
        when(userGateway.existsByEmail(request.email())).thenReturn(true);

        assertThrows(DuplicationException.class, () -> useCase.create(request));

        verify(passwordEncoder, never()).encode(any());
        verify(createCompanyUseCase, never()).normalizeCNPJ(any());
        verify(createCompanyUseCase, never()).createCompany(any());
        verify(userGateway, never()).save(any());
    }

    @Test
    void rejectsPasswordsThatDoNotMeetThePolicy() {
        CreateUserRequest baseRequest = request(UserRole.USER);
        for (String password : new String[]{
                "weak", "lowercase1@", "UPPERCASE1@", "NoNumber@", "NoSpecial123", "Short1@"
        }) {
            CreateUserRequest request = new CreateUserRequest(
                    baseRequest.username(),
                    baseRequest.email(),
                    password,
                    baseRequest.role(),
                    baseRequest.cnpj(),
                    baseRequest.companyName(),
                    baseRequest.companyId()
            );

            assertThrows(IllegalArgumentException.class, () -> useCase.create(request), password);
        }

        verify(passwordEncoder, never()).encode(any());
        verify(createCompanyUseCase, never()).createCompany(any());
        verify(userGateway, never()).save(any());
    }

    @Test
    void rejectsRegistrationWhenCompanyAlreadyExists() {
        CreateUserRequest request = request(UserRole.USER);
        when(userGateway.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(createCompanyUseCase.normalizeCNPJ(FORMATTED_CNPJ)).thenReturn(NORMALIZED_CNPJ);
        when(companyGateway.findByCnpj(NORMALIZED_CNPJ))
                .thenReturn(new Company(UUID.randomUUID(), request.companyName(), NORMALIZED_CNPJ));

        assertThrows(RuntimeException.class, () -> useCase.create(request));

        verify(createCompanyUseCase, never()).createCompany(any());
        verify(userGateway, never()).save(any());
    }

    @Test
    void doesNotSaveUserWhenCompanyCreationFails() {
        CreateUserRequest request = request(UserRole.USER);
        when(userGateway.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(createCompanyUseCase.normalizeCNPJ(FORMATTED_CNPJ)).thenReturn(NORMALIZED_CNPJ);
        when(companyGateway.findByCnpj(NORMALIZED_CNPJ)).thenReturn(null);
        when(createCompanyUseCase.createCompany(any()))
                .thenThrow(new DuplicationException("Company already exists"));

        assertThrows(DuplicationException.class, () -> useCase.create(request));

        verify(userGateway, never()).save(any());
    }

    @Test
    void propagatesUserPersistenceFailureAfterCompanyCreation() {
        CreateUserRequest request = request(UserRole.USER);
        when(userGateway.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(createCompanyUseCase.normalizeCNPJ(FORMATTED_CNPJ)).thenReturn(NORMALIZED_CNPJ);
        when(companyGateway.findByCnpj(NORMALIZED_CNPJ)).thenReturn(null);
        when(createCompanyUseCase.createCompany(any()))
                .thenReturn(new Company(COMPANY_ID, request.companyName(), NORMALIZED_CNPJ));
        when(userGateway.save(any(CreateUserRequest.class)))
                .thenThrow(new IllegalStateException("User persistence failed"));

        assertThrows(IllegalStateException.class, () -> useCase.create(request));

        verify(createCompanyUseCase).createCompany(
                new CreateCompanyRequest(request.companyName(), request.cnpj())
        );
    }

    private CreateUserRequest request(UserRole role) {
        return new CreateUserRequest(
                "Monaly Admin",
                "admin@monaly.com",
                RAW_PASSWORD,
                role,
                FORMATTED_CNPJ,
                "Mercado Monaly",
                null
        );
    }
}
