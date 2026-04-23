package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import core.clients.APIClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateBookingTests {


    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;
    private NewBooking newBooking;



    @BeforeEach
    public void setup(){
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        newBooking = new NewBooking();
        newBooking.setFirstname("John");
        newBooking.setLastname("Doe");
        newBooking.setTotalprice(145);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2025-01-01", "2025-01-07" ));
        newBooking.setAdditionalneeds("Breakfast");
    }

    @Test
    public void createBooking() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        assertThat(response.getStatusCode()).isEqualTo(200);

        String responceBody = response.asString();
        createdBooking = objectMapper.readValue(responceBody, CreatedBooking.class);

        assertThat(createdBooking).isNotNull();
        assertEquals(createdBooking.getBooking().getFirstname(),newBooking.getFirstname());
        assertEquals(createdBooking.getBooking().getLastname(),newBooking.getLastname());
        assertEquals(createdBooking.getBooking().getTotalprice(),newBooking.getTotalprice());
        assertEquals(createdBooking.getBooking().isDepositpaid(),newBooking.isDepositpaid());
        assertEquals(createdBooking.getBooking().getAdditionalneeds(),newBooking.getAdditionalneeds());
        assertEquals(createdBooking.getBooking().getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(createdBooking.getBooking().getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
    }

    @AfterEach
    public void tearDown() {
        apiClient.createToken("admin","password123");
        apiClient.deleteBooking(createdBooking.getBookingid());


        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);
    }
}
