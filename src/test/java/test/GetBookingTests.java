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
        apiClient.createToken("admin", "password123");
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
    @Test
    public void testGetBookingById() throws Exception{
        int id = 6;
        Response response = apiClient.getBookingById(id);
        assertThat(response.getStatusCode()).isEqualTo(200);
        Booking booking = objectMapper.readValue(response.getBody().asString(), Booking.class);
        assertThat(booking).isNotNull();
        assertThat(booking.getFirstname()).isNotBlank();
        assertThat(booking.getTotalprice()).isGreaterThan(0);
        assertThat(booking.getBookingdates()).isNotNull();
        assertThat(booking.getBookingdates().getCheckin()).isNotBlank();
        assertThat(booking.getBookingdates().getCheckout()).isNotBlank();
    }


}
