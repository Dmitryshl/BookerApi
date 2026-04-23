package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingById {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private NewBooking newBooking;
    private CreatedBooking createdBooking;

    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");
        newBooking = new NewBooking();
        newBooking.setFirstname("Johny");
        newBooking.setLastname("Doel");
        newBooking.setTotalprice(115);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2025-02-01", "2025-02-07"));
        newBooking.setAdditionalneeds("Dinner");
    }

    @Test
    public void testGetBookingById() throws Exception {
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response createResponse = apiClient.createBooking(requestBody);
        createdBooking = objectMapper.readValue(createResponse.asString(), CreatedBooking.class);
        int bookingId = createdBooking.getBookingid();
        Response response = apiClient.getBookingById(bookingId);
        assertThat(response.getStatusCode()).isEqualTo(200);

        NewBooking booking = objectMapper.readValue(response.asString(), NewBooking.class);
        assertThat(booking).isNotNull();
        assertThat(booking.getFirstname()).isNotBlank();
        assertThat(booking.getTotalprice()).isGreaterThan(0);
        assertThat(booking.getBookingdates()).isNotNull();
        assertThat(booking.getBookingdates().getCheckin()).isNotBlank();
        assertThat(booking.getBookingdates().getCheckout()).isNotBlank();
    }
    @AfterEach
    public void tearDown() {
        apiClient.createToken("admin","password123");
        apiClient.deleteBooking(createdBooking.getBookingid());


        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);
    }

}



