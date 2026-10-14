package com.monaly.erpmonaly.api.controllers.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.application.usecases.companies.ListCompaniesUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.shared.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompaniesController.class)
@Import(SecurityConfig.class)
class CompaniesControllerTest {

    private static final String VALID_CNPJ = "11.222.333/0001-81";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCompanyUseCase createCompanyUseCase;

    @MockitoBean
    private ListCompaniesUseCase listCompaniesUseCase;

    @Test
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
    void rejectsBlankName() throws Exception {
        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest(" ", VALID_CNPJ)))
                .andExpect(status().isBadRequest());

        verify(createCompanyUseCase, never()).createCompany(any(CreateCompanyRequest.class));
    }

    @Test
    void rejectsNameLongerThan150Characters() throws Exception {
        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("a".repeat(151), VALID_CNPJ)))
                .andExpect(status().isBadRequest());

        verify(createCompanyUseCase, never()).createCompany(any(CreateCompanyRequest.class));
    }

    @Test
    void rejectsInvalidCnpj() throws Exception {
        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("Mercado Monaly", "11.222.333/0001-00")))
                .andExpect(status().isBadRequest());

        verify(createCompanyUseCase, never()).createCompany(any(CreateCompanyRequest.class));
    }

    @Test
    void returnsConflictWhenCnpjAlreadyExists() throws Exception {
        when(createCompanyUseCase.createCompany(any(CreateCompanyRequest.class)))
                .thenThrow(new DuplicationException("Company already exists"));

        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest("Mercado Monaly", VALID_CNPJ)))
                .andExpect(status().isConflict());

        verify(createCompanyUseCase).createCompany(any(CreateCompanyRequest.class));
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
