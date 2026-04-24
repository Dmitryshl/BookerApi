package test;

import core.clients.APIClient;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;


public class HealthCheckTests {
    private APIClient apiClient;

    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
    }

    //тест на метод Ping
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Dmitry")
    public void testPing() {
        step("Пинг тест", () -> {
            Response response = apiClient.ping();
            assertThat(response.getStatusCode()).isEqualTo(201);
        });
    }
}
