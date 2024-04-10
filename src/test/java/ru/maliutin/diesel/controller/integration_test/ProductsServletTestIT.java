package ru.maliutin.diesel.controller.integration_test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Интеграционные тесты сервлета Products
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductsServletTestIT {

    private final String BASE_URI = "http://localhost:8080/product";

    /**
     * Получение списка товаров.
     */
    @Test
    public void doGetExpectListProductsStatusOk() throws Exception {
        String expectedBody = "[{\"title\":\"Накладка тормозная (МАЗ Супер) с заклепками (к-т)  В-160\",\"catalogueNumber\":\"5336-3501105р\",\"programNumber\":6526,\"technics\":[{\"title\":\"МАЗ\"}],\"balance\":15,\"price\":12450.00},{\"title\":\"Муфта выключения сцепления в сборе (МАЗ)\",\"catalogueNumber\":\"236-1601180-Б2\",\"programNumber\":6010,\"technics\":[{\"title\":\"МАЗ\"}],\"balance\":7,\"price\":1400.00},{\"title\":\"Диск сцепления 182 (МАЗ Супер, Евро) d=42 мм.\",\"catalogueNumber\":\"182-1601130\",\"programNumber\":8254,\"technics\":[{\"title\":\"МАЗ\"},{\"title\":\"МТЗ\"}],\"balance\":3,\"price\":7500.00},{\"title\":\"Диск сцепления 184 (МАЗ Супер, Евро) d=51 мм. 111\",\"catalogueNumber\":\"184-1601130\",\"programNumber\":8256,\"technics\":[{\"title\":\"МАЗ\"}],\"balance\":15,\"price\":200.00}]\n";

        HttpResponse<String> actualResponse = performGetRequest(BASE_URI);
        String actualBody = actualResponse.body().replaceAll("\\r\\n|\\r|\\n", "\n");

        expectedBody = expectedBody.replaceAll("\\r\\n|\\r|\\n", "\n");

        assertEquals(200, actualResponse.statusCode());
        assertEquals(expectedBody, actualBody);
    }

    /**
     * Успешное добавление нового товара.
     */
    @Test
    public void doPostExpectProductStatusOk() throws Exception {
        String expectedBody = "{\"title\":\"Накладка тормозная (Камаз) с заклепками (к-т)  В-140\",\"catalogueNumber\":\"5320-3501105р\",\"programNumber\":3856,\"technics\":[{\"title\":\"Камаз\"}],\"balance\":3,\"price\":2500.00}\n";

        HttpResponse<String> actualResponse = performPostRequest(BASE_URI, expectedBody);
        String actualBody = actualResponse.body().replaceAll("\\r\\n|\\r|\\n", "\n");

        expectedBody = expectedBody.replaceAll("\\r\\n|\\r|\\n", "\n");

        assertEquals(200, actualResponse.statusCode());
        assertEquals(expectedBody, actualBody);
    }

    /**
     * Некорректное добавление нового товара,
     * с ошибками валидации.
     */
    @Test
    public void doPostExpectValidationProductExceptionStatusBadRequest() throws Exception {
        String requestBody = """
                {
                  "title": "",
                  "catalogueNumber": "182-1601130",
                  "programNumber": "",
                  "technics": [
                    {"title": "МАЗ"},
                    {"title": "МТЗ"}
                  ],
                  "balance": -1,
                  "price": 0
                }
                """;
        HttpResponse<String> actualResponse = performPostRequest(BASE_URI, requestBody);

        assertEquals(400, actualResponse.statusCode());
    }

    /**
     * Служебный метод формирования Get запроса.
     */
    private HttpResponse<String> performGetRequest(String url) throws IOException, InterruptedException {
         HttpClient httpClient = HttpClient.newHttpClient();
         HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).build();
         return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Служебный метод формирования Post запроса.
     */
    private HttpResponse<String> performPostRequest(String url, String body) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}
