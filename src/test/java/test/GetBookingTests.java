package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingTests {
    private APIClient  apiClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetBooking() throws Exception {
        Response response = apiClient.getBooking();

    assertThat(response.getStatusCode()).isEqualTo(200);


    String responceBody = response.getBody().asString();
            List<Booking> bookings = objectMapper.readValue(responceBody, new TypeReference<List<Booking>>(){});

            assertThat(bookings).isNotEmpty();

            for(Booking booking : bookings) {
                assertThat(booking.getBookingId()).isGreaterThan(0);
            }

    }
}
