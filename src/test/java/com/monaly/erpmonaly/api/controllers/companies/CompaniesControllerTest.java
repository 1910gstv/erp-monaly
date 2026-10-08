package com.monaly.erpmonaly.api.controllers.companies;

import com.monaly.erpmonaly.application.DTOs.companies.CreateCompanyRequest;
import com.monaly.erpmonaly.application.usecases.companies.CreateCompanyUseCase;
import com.monaly.erpmonaly.application.usecases.companies.ListCompaniesUseCase;
import com.monaly.erpmonaly.domain.entities.companies.Company;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompaniesController.class)
@Import(SecurityConfig.class)
class CompaniesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCompanyUseCase createCompanyUseCase;

    @MockitoBean
    private ListCompaniesUseCase listCompaniesUseCase;

    @Test
    void createsCompanyThroughHttpEndpoint() throws Exception {
        Company company = new Company(UUID.randomUUID(), "Mercado Monaly", "12345678000199");
        when(createCompanyUseCase.createCompany(any(CreateCompanyRequest.class))).thenReturn(company);

        mockMvc.perform(post("/companies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Mercado Monaly",
                                  "cnpj": "12345678000199"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(company.getId().toString()))
                .andExpect(jsonPath("$.name").value("Mercado Monaly"))
                .andExpect(jsonPath("$.cnpj").value("12345678000199"));

        verify(createCompanyUseCase).createCompany(any(CreateCompanyRequest.class));
    }
}
