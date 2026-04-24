package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;
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
        booking1.setFirstname("Petrushka");
        booking1.setLastname("Goroshkov");
        booking1.setTotalprice(100);
        booking1.setDepositpaid(true);
        booking1.setBookingdates(new BookingDates("2027-05-03", "2027-06-02"));
        booking1.setAdditionalneeds("Breakfast");

        Response response1 = apiClient.createBooking(objectMapper.writeValueAsString(booking1));
        CreatedBooking created1 = objectMapper.readValue(response1.asString(), CreatedBooking.class);
        bookingIds.add(created1.getBookingid());

        NewBooking booking2 = new NewBooking();
        booking2.setFirstname("Luchok");
        booking2.setLastname("Petrushkin");
        booking2.setTotalprice(100);
        booking2.setDepositpaid(true);
        booking2.setBookingdates(new BookingDates("2027-04-02", "2027-04-05"));
        booking2.setAdditionalneeds("Breakfast");

        Response response2 = apiClient.createBooking(objectMapper.writeValueAsString(booking2));
        CreatedBooking created2 = objectMapper.readValue(response2.asString(), CreatedBooking.class);
        bookingIds.add(created2.getBookingid());

        NewBooking booking3 = new NewBooking();
        booking3.setFirstname("Pomidorchik");
        booking3.setLastname("Ogurechik");
        booking3.setTotalprice(100);
        booking3.setDepositpaid(true);
        booking3.setBookingdates(new BookingDates("2025-05-01", "2025-05-03"));
        booking3.setAdditionalneeds("Breakfast");

        Response response3 = apiClient.createBooking(objectMapper.writeValueAsString(booking3));
        CreatedBooking created3 = objectMapper.readValue(response3.asString(), CreatedBooking.class);
        bookingIds.add(created3.getBookingid());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Dmitry")
    public void testFilterByFirstname() {
        step("Тест фильтра по имени", () -> {
            Response response = apiClient.getBookingWithParam("firstname", "Petrushka");
            assertThat(response.getStatusCode()).isEqualTo(200);
            List<Integer> ids = response.jsonPath().getList("bookingid");
            assertThat(ids).anyMatch(id -> bookingIds.contains(id));
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Dmitry")
    public void testFilterByLastname() {
        step("Тест фильтра по фамилии", () -> {
            Response response = apiClient.getBookingWithParam("lastname", "Ogurechik");
            assertThat(response.getStatusCode()).isEqualTo(200);
            List<Integer> ids = response.jsonPath().getList("bookingid");
            assertThat(ids).anyMatch(id -> bookingIds.contains(id));
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Dmitry")
    public void testFilterByCheckin() {
        step("Тест фильтра по дате заезда", () -> {
            Response response = apiClient.getBookingWithParam("checkin", "2027-05-03");
            assertThat(response.getStatusCode()).isEqualTo(200);
            List<Integer> ids = response.jsonPath().getList("bookingid", Integer.class);
            List<Integer> matchingIds = ids.stream()
                    .filter(bookingIds::contains)
                    .toList();
            for (Integer id : matchingIds) {
                Response r = apiClient.getBookingById(id);
                String checkin = r.jsonPath().getString("bookingdates.checkin");
                assertThat(checkin).isEqualTo("2027-05-03");
            }
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Dmitry")
    public void testFilterByCheckout() {
        step("Тест фильтра по дате выезда", () -> {
            Response response = apiClient.getBookingWithParam("checkout", "2025-05-03");
            assertThat(response.getStatusCode()).isEqualTo(200);
            List<Integer> ids = response.jsonPath().getList("bookingid", Integer.class);
            List<Integer> matchingIds = ids.stream()
                    .filter(bookingIds::contains)
                    .toList();
            for (Integer id : matchingIds) {
                Response r = apiClient.getBookingById(id);
                String checkout = r.jsonPath().getString("bookingdates.checkout");
                assertThat(checkout).isEqualTo("2025-05-03");
            }
        });
    }

    @AfterEach
    public void tearDown() {

        apiClient.createToken("admin", "password123");

        for (Integer id : bookingIds) {
            apiClient.deleteBooking(id);
        }
    }
}