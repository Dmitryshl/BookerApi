package test;

import core.clients.APIClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class HealthCheckTests {
    private APIClient apiClient;

    @BeforeEach
    public void setup() {apiClient = new APIClient();}

    //тест на метод Ping
    @Test
    public void testPing(){
        Response response = apiClient.ping();
        assertThat(response.getStatusCode()).isEqualTo(201);
    }
}
