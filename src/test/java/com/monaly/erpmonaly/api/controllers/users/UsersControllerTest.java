package com.monaly.erpmonaly.api.controllers.users;

import com.monaly.erpmonaly.application.DTOs.users.CreateUserResponse;
import com.monaly.erpmonaly.application.usecases.users.CreateUserUseCase;
import com.monaly.erpmonaly.domain.enums.UserRole;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.shared.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
@Import(SecurityConfig.class)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @Test
    void rejectsInvalidEmailBeforeCallingUseCase() throws Exception {
        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequest("invalid-email", "StrongPass1@", "USER")))
                .andExpect(status().isBadRequest());

        verify(createUserUseCase, never()).create(any());
    }

    @Test
    void returnsConflictWhenEmailIsAlreadyInUse() throws Exception {
        when(createUserUseCase.create(any()))
                .thenThrow(new DuplicationException("O email informado já existe, tente outro"));

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequest("admin@monaly.com", "StrongPass1@", "USER")))
                .andExpect(status().isConflict());

        verify(createUserUseCase).create(any());
    }

    @Test
    void returnsUserWithoutPasswordOrHash() throws Exception {
        when(createUserUseCase.create(any()))
                .thenReturn(new CreateUserResponse("Monaly Admin", "admin@monaly.com", UserRole.ADMIN));

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequest("admin@monaly.com", "StrongPass1@", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Monaly Admin"))
                .andExpect(jsonPath("$.email").value("admin@monaly.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.hash").doesNotExist());
    }

    private String userRequest(String email, String password, String role) {
        return """
                {
                  "username": "Monaly Admin",
                  "email": "%s",
                  "password": "%s",
                  "role": "%s",
                  "cnpj": "11.222.333/0001-81",
                  "companyName": "Mercado Monaly"
                }
                """.formatted(email, password, role);
    }
}
