package com.monaly.erpmonaly;

import com.monaly.erpmonaly.application.gateways.users.UserGateway;
import com.monaly.erpmonaly.infra.persistence.CompanyRepository;
import com.monaly.erpmonaly.infra.persistence.UserRepository;
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
import java.util.UUID;
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
class UserConcurrentRegistrationIT {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

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
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @MockitoSpyBean
    private UserGateway userGateway;

    @Test
    void concurrentRegistrationOfSameEmailReturnsOneSuccessAndOneConflict() throws Exception {
        String email = "concurrent-" + UUID.randomUUID() + "@monaly.com";
        String firstCnpj = formatCnpj(123456780001L);
        String secondCnpj = formatCnpj(987654320001L);
        CyclicBarrier bothRequestsCheckedEmail = new CyclicBarrier(2);

        doAnswer(invocation -> {
            boolean emailExists = (boolean) invocation.callRealMethod();
            bothRequestsCheckedEmail.await(10, TimeUnit.SECONDS);
            return emailExists;
        }).when(userGateway).existsByEmail(eq(email));

        CountDownLatch requestsReady = new CountDownLatch(2);
        CountDownLatch startRequests = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<HttpResponse<String>> firstRequest = executor.submit(
                    () -> sendRequestWhenReleased(
                            requestsReady,
                            startRequests,
                            email,
                            firstCnpj,
                            "Concurrent Company One"
                    )
            );
            Future<HttpResponse<String>> secondRequest = executor.submit(
                    () -> sendRequestWhenReleased(
                            requestsReady,
                            startRequests,
                            email,
                            secondCnpj,
                            "Concurrent Company Two"
                    )
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
                    "Exactly one request should create the user");
            assertEquals(1, statuses.stream().filter(status -> status == 409).count(),
                    "The concurrent duplicate email should receive HTTP 409");
            assertEquals(1, userRepository.findAll().stream()
                            .filter(user -> email.equals(user.getEmail()))
                            .count(),
                    "Only one user with the email should be persisted");
            assertEquals(1, companyRepository.findAll().stream()
                            .filter(company -> company.getCnpj().equals(firstCnpj.replaceAll("\\D", ""))
                                    || company.getCnpj().equals(secondCnpj.replaceAll("\\D", "")))
                            .count(),
                    "The losing registration should roll back its company creation");
        } finally {
            startRequests.countDown();
            executor.shutdownNow();
        }
    }

    private HttpResponse<String> sendRequestWhenReleased(
            CountDownLatch requestsReady,
            CountDownLatch startRequests,
            String email,
            String cnpj,
            String companyName
    ) throws InterruptedException, IOException {
        requestsReady.countDown();
        if (!startRequests.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Timed out waiting to start concurrent HTTP requests");
        }

        String requestBody = """
                {
                  "username": "Concurrent Admin",
                  "email": "%s",
                  "password": "StrongPass1@",
                  "role": "USER",
                  "cnpj": "%s",
                  "companyName": "%s"
                }
                """.formatted(email, cnpj, companyName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String formatCnpj(long base) {
        String digits = "%012d".formatted(base);
        int firstCheckDigit = calculateCheckDigit(digits, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        String withFirstCheckDigit = digits + firstCheckDigit;
        int secondCheckDigit = calculateCheckDigit(
                withFirstCheckDigit,
                new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
        );
        String cnpj = withFirstCheckDigit + secondCheckDigit;
        return "%s.%s.%s/%s-%s".formatted(
                cnpj.substring(0, 2),
                cnpj.substring(2, 5),
                cnpj.substring(5, 8),
                cnpj.substring(8, 12),
                cnpj.substring(12)
        );
    }

    private int calculateCheckDigit(String digits, int[] weights) {
        int sum = 0;
        for (int index = 0; index < weights.length; index++) {
            sum += Character.getNumericValue(digits.charAt(index)) * weights[index];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
