package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingFilterTests {

    private APIClient apiClient;
    private ObjectMapper objectMapper;

    private List<Integer> bookingIds;

    @BeforeEach
    public void setup() throws Exception {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        bookingIds = new ArrayList<>();
        apiClient.createToken("admin","password123");

        NewBooking booking1 = new NewBooking();
        booking1.setFirstname("Ivan");
        booking1.setLastname("Ivanov");
        booking1.setTotalprice(100);
        booking1.setDepositpaid(true);
        booking1.setBookingdates(new BookingDates("2025-01-01", "2025-01-05"));
        booking1.setAdditionalneeds("Breakfast");

        Response response1 = apiClient.createBooking(objectMapper.writeValueAsString(booking1));
        CreatedBooking created1 = objectMapper.readValue(response1.asString(), CreatedBooking.class);
        bookingIds.add(created1.getBookingid());

        NewBooking booking2 = new NewBooking();
        booking2.setFirstname("Ivan");
        booking2.setLastname("Sidorov");
        booking2.setTotalprice(100);
        booking2.setDepositpaid(true);
        booking2.setBookingdates(new BookingDates("2025-01-01", "2025-01-10"));
        booking2.setAdditionalneeds("Breakfast");

        Response response2 = apiClient.createBooking(objectMapper.writeValueAsString(booking2));
        CreatedBooking created2 = objectMapper.readValue(response2.asString(), CreatedBooking.class);
        bookingIds.add(created2.getBookingid());

        NewBooking booking3 = new NewBooking();
        booking3.setFirstname("Petr");
        booking3.setLastname("Petrov");
        booking3.setTotalprice(100);
        booking3.setDepositpaid(true);
        booking3.setBookingdates(new BookingDates("2025-02-01", "2025-02-05"));
        booking3.setAdditionalneeds("Breakfast");

        Response response3 = apiClient.createBooking(objectMapper.writeValueAsString(booking3));
        CreatedBooking created3 = objectMapper.readValue(response3.asString(), CreatedBooking.class);
        bookingIds.add(created3.getBookingid());
    }

    @Test
    public void testFilterByFirstname() {

        Response response = apiClient.getBookingWithParam("firstname", "Ivan");

        assertThat(response.getStatusCode()).isEqualTo(200);

        List<Integer> ids = response.jsonPath().getList("bookingid");

        boolean found = false;

        for (Integer id : ids) {
            Response r = apiClient.getBookingById(id);
            String name = r.jsonPath().getString("firstname");

            if (name.equals("Ivan")) {
                found = true;
                break;
            }
        }

        assertThat(found).isTrue();
    }

    @Test
    public void testFilterByLastname() {

        Response response = apiClient.getBookingWithParam("lastname", "Petrov");

        assertThat(response.getStatusCode()).isEqualTo(200);

        List<Integer> ids = response.jsonPath().getList("bookingid");

        boolean found = false;

        for (Integer id : ids) {
            Response r = apiClient.getBookingById(id);
            String name = r.jsonPath().getString("lastname");

            if (name.equals("Petrov")) {
                found = true;
                break;
            }
        }

        assertThat(found).isTrue();
    }

    @Test
    public void testFilterByCheckin() {

        Response response = apiClient.getBookingWithParam("checkin", "2025-01-01");

        assertThat(response.getStatusCode()).isEqualTo(200);

        List<Integer> ids = response.jsonPath().getList("bookingid");

        boolean found = false;

        for (Integer id : ids) {
            Response r = apiClient.getBookingById(id);
            String date = r.jsonPath().getString("bookingdates.checkin");

            if (date.equals("2025-01-01")) {
                found = true;
                break;
            }
        }

        assertThat(found).isTrue();
    }

    @Test
    public void testFilterByCheckout() {

        Response response = apiClient.getBookingWithParam("checkout", "2025-01-10");

        assertThat(response.getStatusCode()).isEqualTo(200);

        List<Integer> ids = response.jsonPath().getList("bookingid");

        boolean found = false;

        for (Integer id : ids) {
            Response r = apiClient.getBookingById(id);
            String date = r.jsonPath().getString("bookingdates.checkout");

            if (date.equals("2025-01-10")) {
                found = true;
                break;
            }
        }

        assertThat(found).isTrue();
    }

    @AfterEach
    public void tearDown() {

        apiClient.createToken("admin", "password123");

        for (Integer id : bookingIds) {
            apiClient.deleteBooking(id);
        }
    }
}