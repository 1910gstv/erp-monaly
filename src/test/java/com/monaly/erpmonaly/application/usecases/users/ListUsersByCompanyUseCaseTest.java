package com.monaly.erpmonaly.application.usecases.users;

import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.domain.entities.users.User;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListUsersByCompanyUseCaseTest {

    private UserGateway userGateway;
    private ListUsersByCompanyUseCase useCase;

    @BeforeEach
    void setUp() {
        userGateway = mock(UserGateway.class);
        useCase = new ListUsersByCompanyUseCase(mock(CompanyGateway.class), userGateway);
    }

    @Test
    void companyUserListsOnlyUsersFromAuthenticatedCompany() {
        UUID authenticatedCompanyId = UUID.randomUUID();
        UUID anotherCompanyId = UUID.randomUUID();
        UserEntity userEntity = authenticatedUser(UserRole.USER, authenticatedCompanyId);
        when(userGateway.findAll(authenticatedCompanyId)).thenReturn(List.of());

        List<User> result = useCase.listAllUsersByCompany(userEntity);

        assertEquals(List.of(), result);
        verify(userGateway).findAll(authenticatedCompanyId);
        verify(userGateway, never()).findAll(anotherCompanyId);
        verify(userGateway, never()).findAll();
    }

    @Test
    void companyAdminListsOnlyUsersFromAuthenticatedCompany() {
        UUID authenticatedCompanyId = UUID.randomUUID();
        UserEntity admin = authenticatedUser(UserRole.ADMIN, authenticatedCompanyId);
        List<User> companyUsers = List.of(user(authenticatedCompanyId));
        when(userGateway.findAll(authenticatedCompanyId)).thenReturn(companyUsers);

        List<User> result = useCase.listAllUsersByCompany(admin);

        assertEquals(companyUsers, result);
        verify(userGateway).findAll(authenticatedCompanyId);
        verify(userGateway, never()).findAll();
    }

    @Test
    void masterCanListUsersAcrossCompanies() {
        UUID masterCompanyId = UUID.randomUUID();
        UserEntity master = authenticatedUser(UserRole.MASTER, masterCompanyId);
        List<User> users = List.of(user(masterCompanyId), user(UUID.randomUUID()));
        when(userGateway.findAll()).thenReturn(users);

        List<User> result = useCase.listAllUsersByCompany(master);

        assertEquals(users, result);
        verify(userGateway).findAll();
        verify(userGateway, never()).findAll(masterCompanyId);
    }

    private UserEntity authenticatedUser(UserRole role, UUID companyId) {
        return new UserEntity(UUID.randomUUID(), "Authenticated User", "authenticated@monaly.com",
                "hashed", role, companyId);
    }

    private User user(UUID companyId) {
        return new User(UUID.randomUUID(), "Listed User", UUID.randomUUID() + "@monaly.com",
                "", UserRole.USER, companyId);
    }
}
