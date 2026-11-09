package com.monaly.erpmonaly.application.usecases.users;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserInCompanyRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateUserInCompanyUseCaseTest {

    private static final UUID COMPANY_ID = UUID.randomUUID();
    private static final String RAW_PASSWORD = "StrongPass1@";
    private static final String HASHED_PASSWORD = "$2a$hashed-password";

    private UserGateway userGateway;
    private CompanyGateway companyGateway;
    private PasswordEncoder passwordEncoder;
    private CreateUserInCompanyUseCase useCase;

    @BeforeEach
    void setUp() {
        userGateway = mock(UserGateway.class);
        companyGateway = mock(CompanyGateway.class);
        passwordEncoder = mock(PasswordEncoder.class);
        useCase = new CreateUserInCompanyUseCase(
                userGateway,
                companyGateway,
                passwordEncoder,
                mock(CreateCompanyUseCase.class)
        );
        when(companyGateway.findById(COMPANY_ID)).thenReturn(Optional.of(company()));
        when(userGateway.existsByEmail("new-user@monaly.com")).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userGateway.save(any(CreateUserRequest.class)))
                .thenAnswer(invocation -> {
                    CreateUserRequest request = invocation.getArgument(0);
                    return new CreateUserResponse(request.username(), request.email(), request.role());
                });
    }

    @Test
    void companyAdminCanChooseUserRoleForNewAccount() throws Exception {
        CreateUserInCompanyRequest request = request(UserRole.USER);
        UserEntity admin = authenticatedUser(UserRole.ADMIN, COMPANY_ID);

        CreateUserResponse response = useCase.createUser(request, COMPANY_ID, admin);

        assertEquals(UserRole.USER, response.role());
        var savedRequest = org.mockito.ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(userGateway).save(savedRequest.capture());
        assertEquals(UserRole.USER, savedRequest.getValue().role());
        assertEquals(COMPANY_ID, savedRequest.getValue().companyId());
        assertEquals(HASHED_PASSWORD, savedRequest.getValue().password());
    }

    @Test
    void companyAdminCanCreateAnotherAdministrator() throws Exception {
        CreateUserInCompanyRequest request = request(UserRole.ADMIN);
        UserEntity admin = authenticatedUser(UserRole.ADMIN, COMPANY_ID);

        CreateUserResponse response = useCase.createUser(request, COMPANY_ID, admin);

        assertEquals(UserRole.ADMIN, response.role());
        var savedRequest = org.mockito.ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(userGateway).save(savedRequest.capture());
        assertEquals(UserRole.ADMIN, savedRequest.getValue().role());
    }

    @Test
    void nonAdminCannotCreateUserEvenWhenRequestedRoleIsAdmin() {
        UserEntity user = authenticatedUser(UserRole.USER, COMPANY_ID);

        assertThrows(AccessDeniedException.class,
                () -> useCase.createUser(request(UserRole.ADMIN), COMPANY_ID, user));

        verify(userGateway, never()).existsByEmail(any());
        verify(userGateway, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void adminCannotCreateUserInAnotherCompany() {
        UserEntity admin = authenticatedUser(UserRole.ADMIN, UUID.randomUUID());

        assertThrows(AccessDeniedException.class,
                () -> useCase.createUser(request(UserRole.USER), COMPANY_ID, admin));

        verify(userGateway, never()).existsByEmail(any());
        verify(userGateway, never()).save(any());
    }

    @Test
    void cannotCreateUserForUnknownCompany() {
        when(companyGateway.findById(COMPANY_ID)).thenReturn(Optional.empty());
        UserEntity admin = authenticatedUser(UserRole.ADMIN, COMPANY_ID);

        assertThrows(DuplicationException.class,
                () -> useCase.createUser(request(UserRole.USER), COMPANY_ID, admin));

        verify(userGateway, never()).existsByEmail(any());
        verify(userGateway, never()).save(any());
    }

    private CompanyEntity company() {
        OffsetDateTime now = OffsetDateTime.now();
        return new CompanyEntity(COMPANY_ID, "Mercado Monaly", "11222333000181", now, now);
    }

    private UserEntity authenticatedUser(UserRole role, UUID companyId) {
        return new UserEntity(UUID.randomUUID(), "Authenticated User", "authenticated@monaly.com",
                "hashed", role, companyId);
    }

    private CreateUserInCompanyRequest request(UserRole role) {
        return new CreateUserInCompanyRequest("New User", "new-user@monaly.com", RAW_PASSWORD, role);
    }
}
