package com.monaly.erpmonaly;

import com.monaly.erpmonaly.application.gateways.companies.CompanyGateway;
import com.monaly.erpmonaly.domain.entities.companies.Company;
import com.monaly.erpmonaly.infra.persistence.CompanyEntity;
import com.monaly.erpmonaly.infra.persistence.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CompanyConcurrentRegistrationIT {

    private static final String CNPJ = "11222333000181";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String REQUEST_BODY = """
            {
              "name": "Concurrent Test Company",
              "cnpj": "11.222.333/0001-81"
            }
            """;

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private CompanyRepository companyRepository;

    @MockitoSpyBean
    private CompanyGateway companyGateway;

    @Test
    void concurrentRegistrationOfSameCnpjReturnsOneSuccessAndOneConflict() throws Exception {
        CompanyEntity existingCompany = companyRepository.findByCnpj(CNPJ);
        if (existingCompany != null) {
            companyRepository.delete(existingCompany);
            companyRepository.flush();
        }

        CyclicBarrier bothRequestsCheckedCnpj = new CyclicBarrier(2);
        doAnswer(invocation -> {
            Company company = (Company) invocation.callRealMethod();
            bothRequestsCheckedCnpj.await(10, TimeUnit.SECONDS);
            return company;
        }).when(companyGateway).findByCnpj(eq(CNPJ));

        CountDownLatch requestsReady = new CountDownLatch(2);
        CountDownLatch startRequests = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<HttpResponse<String>> firstRequest = executor.submit(
                    () -> sendRequestWhenReleased(requestsReady, startRequests)
            );
            Future<HttpResponse<String>> secondRequest = executor.submit(
                    () -> sendRequestWhenReleased(requestsReady, startRequests)
            );

            if (!requestsReady.await(10, TimeUnit.SECONDS)) {
                throw new AssertionError("Both HTTP request workers did not become ready");
            }
            startRequests.countDown();

            List<Integer> statuses = List.of(
                    firstRequest.get(30, TimeUnit.SECONDS).statusCode(),
                    secondRequest.get(30, TimeUnit.SECONDS).statusCode()
            );

            assertEquals(1, statuses.stream().filter(status -> status == 200).count(),
                    "Exactly one request should create the company");
            assertEquals(1, statuses.stream().filter(status -> status == 409).count(),
                    "The concurrent duplicate should receive HTTP 409");
        } finally {
            startRequests.countDown();
            executor.shutdownNow();
        }
    }

    private HttpResponse<String> sendRequestWhenReleased(
            CountDownLatch requestsReady,
            CountDownLatch startRequests
    ) throws InterruptedException, IOException {
        requestsReady.countDown();
        if (!startRequests.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Timed out waiting to start concurrent HTTP requests");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/companies/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(REQUEST_BODY))
                .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
