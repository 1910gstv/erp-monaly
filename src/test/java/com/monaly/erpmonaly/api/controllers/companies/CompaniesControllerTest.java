package com.monaly.erpmonaly.api.controllers.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserInCompanyRequest;
import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.application.usecases.companies.EditCompanyUseCase;
import com.monaly.erpmonaly.application.usecases.companies.ListCompaniesUseCase;
import com.monaly.erpmonaly.application.usecases.users.CreateUserInCompanyUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import com.monaly.erpmonaly.infra.persistence.UserRepository;
import com.monaly.erpmonaly.infra.security.SecurityConfig;
import com.monaly.erpmonaly.infra.security.TokenService;
import com.monaly.erpmonaly.shared.presentation.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CompaniesController.class, HealthController.class})
@Import(SecurityConfig.class)
class CompaniesControllerTest {

    private static final String VALID_CNPJ = "11.222.333/0001-81";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCompanyUseCase createCompanyUseCase;

    @MockitoBean
    private ListCompaniesUseCase listCompaniesUseCase;

    @MockitoBean
    private CreateUserInCompanyUseCase createUserInCompanyUseCase;

    @MockitoBean
    private EditCompanyUseCase editCompanyUseCase;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createsCompanyThroughHttpEndpoint() throws Exception {
        Company company = new Company(UUID.randomUUID(), "Mercado Monaly", "11222333000181");
        when(createCompanyUseCase.createCompany(any(CreateCompanyRequest.class))).thenReturn(company);

        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("Mercado Monaly", VALID_CNPJ)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(company.getId().toString()))
                .andExpect(jsonPath("$.name").value("Mercado Monaly"))
                .andExpect(jsonPath("$.cnpj").value("11222333000181"));

        verify(createCompanyUseCase).createCompany(any(CreateCompanyRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectsBlankName() throws Exception {
        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest(" ", VALID_CNPJ)))
                .andExpect(status().isBadRequest());

        verify(createCompanyUseCase, never()).createCompany(any(CreateCompanyRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectsNameLongerThan150Characters() throws Exception {
        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("a".repeat(151), VALID_CNPJ)))
                .andExpect(status().isBadRequest());

        verify(createCompanyUseCase, never()).createCompany(any(CreateCompanyRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectsInvalidCnpj() throws Exception {
        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("Mercado Monaly", "11.222.333/0001-00")))
                .andExpect(status().isBadRequest());

        verify(createCompanyUseCase, never()).createCompany(any(CreateCompanyRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void returnsConflictWhenCnpjAlreadyExists() throws Exception {
        when(createCompanyUseCase.createCompany(any(CreateCompanyRequest.class)))
                .thenThrow(new DuplicationException("Company already exists"));

        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("Mercado Monaly", VALID_CNPJ)))
                .andExpect(status().isConflict());

        verify(createCompanyUseCase).createCompany(any(CreateCompanyRequest.class));
    }

    @Test
    void allowsUnauthenticatedHealthCheck() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsUnauthenticatedAccessToProtectedCompaniesRoute() throws Exception {
        mockMvc.perform(get("/companies/list"))
                .andExpect(status().isUnauthorized());

        verify(listCompaniesUseCase, never()).listAll(any(UserEntity.class));
    }

    @Test
    void allowsAuthenticatedAccessToProtectedCompaniesRouteWithBearerToken() throws Exception {
        String token = "valid-test-token";
        String email = "admin@monaly.com";
        UserEntity authenticatedUser = new UserEntity(
                UUID.randomUUID(),
                "Monaly Admin",
                email,
                "$2a$hashed-password",
                com.monaly.erpmonaly.domain.enums.UserRole.ADMIN,
                UUID.randomUUID()
        );
        when(tokenService.validateToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(authenticatedUser);
        when(listCompaniesUseCase.listAll(authenticatedUser)).thenReturn(List.of());

        mockMvc.perform(get("/companies/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        verify(listCompaniesUseCase).listAll(authenticatedUser);
    }

    @Test
    void authenticatedUserCanAccessNonAdministrativeCompanyRoute() throws Exception {
        UserEntity user = authenticatedUser(UserRole.USER, UUID.randomUUID());
        authenticate(user);
        when(listCompaniesUseCase.listAll(user)).thenReturn(List.of());

        mockMvc.perform(get("/companies/list")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());

        verify(listCompaniesUseCase).listAll(user);
    }

    @Test
    void unauthenticatedRequestCannotCreateCompanyUser() throws Exception {
        mockMvc.perform(post("/companies/{companyId}/users/create", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserRequest(UserRole.ADMIN)))
                .andExpect(status().isUnauthorized());

        verify(createUserInCompanyUseCase, never()).createUser(
                any(CreateUserInCompanyRequest.class), any(UUID.class), any(UserEntity.class));
    }

    @Test
    void authenticatedUserCannotPromoteItselfByRequestingAdminRole() throws Exception {
        UserEntity user = authenticatedUser(UserRole.USER, UUID.randomUUID());
        authenticate(user);

        mockMvc.perform(post("/companies/{companyId}/users/create", user.getCompanyId())
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserRequest(UserRole.ADMIN)))
                .andExpect(status().isForbidden());

        verify(createUserInCompanyUseCase, never()).createUser(
                any(CreateUserInCompanyRequest.class), any(UUID.class), any(UserEntity.class));
    }

    @Test
    void administratorCanCreateAnotherAdministrator() throws Exception {
        UUID companyId = UUID.randomUUID();
        UserEntity admin = authenticatedUser(UserRole.ADMIN, companyId);
        authenticate(admin);
        CreateUserResponse response = new CreateUserResponse("New Admin", "new-admin@monaly.com", UserRole.ADMIN);
        when(createUserInCompanyUseCase.createUser(
                any(CreateUserInCompanyRequest.class), eq(companyId), eq(admin)))
                .thenReturn(response);

        mockMvc.perform(post("/companies/{companyId}/users/create", companyId)
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserRequest(UserRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(createUserInCompanyUseCase).createUser(
                org.mockito.ArgumentMatchers.argThat(request -> request.role() == UserRole.ADMIN),
                eq(companyId),
                eq(admin));
    }

    @Test
    void accessDeniedByUserCreationUseCaseIsReturnedAsForbidden() throws Exception {
        UUID companyId = UUID.randomUUID();
        UserEntity admin = authenticatedUser(UserRole.ADMIN, UUID.randomUUID());
        authenticate(admin);
        when(createUserInCompanyUseCase.createUser(
                any(CreateUserInCompanyRequest.class), eq(companyId), eq(admin)))
                .thenThrow(new AccessDeniedException("Not allowed"));

        mockMvc.perform(post("/companies/{companyId}/users/create", companyId)
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserRequest(UserRole.ADMIN)))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedRequestCannotEditCompany() throws Exception {
        mockMvc.perform(put("/companies/edit/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("Updated Company", VALID_CNPJ)))
                .andExpect(status().isUnauthorized());

        verify(editCompanyUseCase, never()).editCompany(
                any(CreateCompanyRequest.class), any(UserEntity.class));
    }

    @Test
    void authenticatedNonAdministratorCannotEditCompany() throws Exception {
        UserEntity user = authenticatedUser(UserRole.USER, UUID.randomUUID());
        authenticate(user);

        mockMvc.perform(put("/companies/edit/me")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("Updated Company", VALID_CNPJ)))
                .andExpect(status().isForbidden());

        verify(editCompanyUseCase, never()).editCompany(
                any(CreateCompanyRequest.class), any(UserEntity.class));
    }

    @Test
    void administratorCanEditCompanyThroughProtectedRoute() throws Exception {
        UUID companyId = UUID.randomUUID();
        UserEntity admin = authenticatedUser(UserRole.ADMIN, companyId);
        authenticate(admin);
        Company updatedCompany = new Company(companyId, "Updated Company", "11222333000181");
        when(editCompanyUseCase.editCompany(
                any(CreateCompanyRequest.class), eq(admin)))
                .thenReturn(updatedCompany);

        mockMvc.perform(put("/companies/edit/me")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("Updated Company", VALID_CNPJ)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Company"));

        verify(editCompanyUseCase).editCompany(any(CreateCompanyRequest.class), eq(admin));
    }

    @Test
    void accessDeniedByCompanyUseCaseIsReturnedAsForbidden() throws Exception {
        UserEntity admin = authenticatedUser(UserRole.ADMIN, UUID.randomUUID());
        authenticate(admin);
        when(editCompanyUseCase.editCompany(
                any(CreateCompanyRequest.class), eq(admin)))
                .thenThrow(new AccessDeniedException("Not allowed"));

        mockMvc.perform(put("/companies/edit/me")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("Updated Company", VALID_CNPJ)))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsInvalidCompanyEditRequestBeforeCallingUseCase() throws Exception {
        UserEntity admin = authenticatedUser(UserRole.ADMIN, UUID.randomUUID());
        authenticate(admin);

        mockMvc.perform(put("/companies/edit/me")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest(" ", VALID_CNPJ)))
                .andExpect(status().isBadRequest());

        verify(editCompanyUseCase, never()).editCompany(
                any(CreateCompanyRequest.class), any(UserEntity.class));
    }

    private void authenticate(UserEntity user) {
        when(tokenService.validateToken("test-token")).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
    }

    private UserEntity authenticatedUser(UserRole role, UUID companyId) {
        return new UserEntity(UUID.randomUUID(), role.name(), role.name().toLowerCase() + "@monaly.com",
                "$2a$hashed-password", role, companyId);
    }

    private String createUserRequest(UserRole role) {
        return """
                {
                  "username": "New User",
                  "email": "new-user@monaly.com",
                  "password": "StrongPass1@",
                  "role": "%s"
                }
                """.formatted(role.name());
    }

    private String companyRequest(String name, String cnpj) {
        return """
                {
                  "name": "%s",
                  "cnpj": "%s"
                }
                """.formatted(name, cnpj);
    }
}
