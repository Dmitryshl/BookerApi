package core.clients;

import com.sun.net.httpserver.Request;
import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIClient {

    private final String baseUrl;
    private String token;

    public APIClient () {
        this.baseUrl = determineBaseUrl();
    }

    // Определение базового URL на основе файла конфигурации
    public String determineBaseUrl() {
      String environment = System.getProperty("env","test");
      String configFileName = "application-" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)){
            if (input == null){
                throw new IllegalStateException("Configuration file not found: " + configFileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration file: " + configFileName, e);
        }
            return properties.getProperty("baseUrl");
    }

    //Настройка базовых http запросов
    private RequestSpecification getRequestSpec(){
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .filter(addAuthTokenFilter());

    }

    public void createToken(String username, String password) {
        // Формирование JSON тела для запроса
        String requestBody = String.format("{ \"username\": \"%s\",\"password\": \"%s\" }", username, password);
        // Отправка POST-запроса на эндпоинт для аутентификации и получение токена
        Response response = getRequestSpec()
                .body(requestBody) // Устанавливаем тело запроса
                .when()
                .post(ApiEndpoints.AUTH.getPath()) // POST-запрос на эндпоинт аутентификации
                .then()
                .statusCode(200) // Проверяем, что статус ответа 200 (ОК)
                .extract()
                .response();
        // Извлечение токена из ответа и сохранение в переменной
        token = response.jsonPath().getString("token");
    }

    private Filter addAuthTokenFilter() {
        return (FilterableRequestSpecification requestSpec,
                FilterableResponseSpecification responseSpec, FilterContext ctx) -> {
            if (token != null) {
                requestSpec.header("Cookie", "token=" + token);
            }
            return ctx.next(requestSpec, responseSpec); // Продолжает выполнение запроса
        };
    }


            public Response ping() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.PING.getPath())
                .then()
                .statusCode(201)
                .extract()
                .response();

    }
    public Response getBooking() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPath()) // Используем ENUM для эндпоинта /booking
                .then()
                .statusCode(200) // Ожидаемый статус-код 200 OK
                .extract()
                .response();
    }

    public  Response getBookingById(int bookingid) {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPath() + "/" + bookingid)
                .then()
                .statusCode(200)
                .extract()
                .response();

    }

    public Response deleteBooking(int bookingid) {
        return getRequestSpec()
                .pathParam("id", bookingid)
                .when()
                .delete(ApiEndpoints.BOOKING.getPath() + "/{id}")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .response();

    }
}
