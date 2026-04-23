package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingTests {
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
        newBooking.setFirstname("John");
        newBooking.setLastname("Doe");
        newBooking.setTotalprice(145);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2025-01-01", "2025-01-07"));
        newBooking.setAdditionalneeds("Breakfast");
    }


    @Test
    public void testGetBooking() throws Exception {
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response createResponse = apiClient.createBooking(requestBody);
        createdBooking = objectMapper.readValue(createResponse.asString(), CreatedBooking.class);
        Response response = apiClient.getBooking();

        assertThat(response.getStatusCode()).isEqualTo(200);


        String responceBody = response.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responceBody, new TypeReference<List<Booking>>() {
        });

        assertThat(bookings).isNotEmpty();

        for (Booking booking : bookings) {
            assertThat(booking.getBookingId()).isGreaterThan(0);
        }

    }

    @AfterEach
    public void tearDown() {
            apiClient.deleteBooking(createdBooking.getBookingid());
            assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);
        }
    }




////
//    @Test
//    public void testGetBookingById() throws Exception{
//        int id = 6;
//        Response response = apiClient.getBookingById(id);
//        assertThat(response.getStatusCode()).isEqualTo(200);
//        Booking booking = objectMapper.readValue(response.getBody().asString(), Booking.class);
//        assertThat(booking).isNotNull();
//        assertThat(booking.getFirstname()).isNotBlank();
//        assertThat(booking.getTotalprice()).isGreaterThan(0);
//        assertThat(booking.getBookingdates()).isNotNull();
//        assertThat(booking.getBookingdates().getCheckin()).isNotBlank();
//        assertThat(booking.getBookingdates().getCheckout()).isNotBlank();
//    }
//
//    @Test
//    public void getAllBookingIdAndDeleteRandom() throws Exception{
//        Response response = apiClient.getBooking();
//        String responceBody = response.getBody().asString();
//        List<Booking> bookings = objectMapper.readValue(responceBody, new TypeReference<List<Booking>>(){});
//        Random random = new Random();
//        Booking randomBooking = bookings.get(random.nextInt(bookings.size() -1))   ;
//        int randomId = randomBooking.getBookingId();
//        Response response1 = apiClient.deleteBooking(randomId);
//        assertThat(response1.getStatusCode()).isEqualTo(201);
//        Response response2 = apiClient.getDeletedBooking(randomId);
//        assertThat(response2.getStatusCode()).isEqualTo(404);
//}



