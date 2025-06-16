package integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private WireMockServer wireMockServer;

    private String service2BaseUrl;

    @BeforeEach
    void setUp() {
        // Запуск WireMock сервера
        wireMockServer = new WireMockServer(8085); // порт, на котором будет работать WireMock
        wireMockServer.start();

        // Устанавливаем URL сервиса 2 через WireMock
        service2BaseUrl = "http://localhost:8085";

        // Настройка WireMock: эндпоинт /api/client/status
        wireMockServer.stubFor(get(urlPathEqualTo("/api/client/status"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("\"BLOCKED\"")));
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testGetClientStatus_ReturnsBlocked() {
        // Arrange
        String url = "http://localhost:" + port + "/api/client/status?clientId=C1&accountId=A1";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("BLOCKED");
    }

    @Test
    void testGetClientStatus_Service2ReturnsError() {
        // Arrange
        wireMockServer.resetAll();
        wireMockServer.stubFor(get(urlPathEqualTo("/api/client/status"))
                .willReturn(aResponse()
                        .withStatus(500)));

        String url = "http://localhost:" + port + "/api/client/status?clientId=C1&accountId=A1";

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testGetClientStatus_Service2Timeout() throws InterruptedException {
        // Arrange
        wireMockServer.resetAll();
        wireMockServer.stubFor(get(urlPathEqualTo("/api/client/status"))
                .willReturn(aResponse()
                        .withFixedDelay(5000))); // искусственная задержка

        String url = "http://localhost:" + port + "/api/client/status?clientId=C1&accountId=A1";

        long startTime = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        long duration = System.currentTimeMillis() - startTime;

        // Act & Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(duration).isLessThan(6000L); // меньше 6 секунд (Spring Cloud Gateway Timeout по умолчанию ~5с)
    }
}