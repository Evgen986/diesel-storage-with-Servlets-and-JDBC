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
 * Интеграционные тесты сервлета Product
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductServletIT {

    private final String BASE_URI = "http://localhost:8080/product/";

    /**
     * Успешное получение товара по id.
     */
    @Test
    public void doGetExpectProductStatusOk() throws Exception {
        long productId = 1L;
        String expectedBody = "{\"title\":\"Накладка тормозная (МАЗ Супер) с заклепками (к-т)  В-160\",\"catalogueNumber\":\"5336-3501105р\",\"programNumber\":6526,\"technics\":[{\"title\":\"МАЗ\"}],\"balance\":15,\"price\":12450.00}\n";

        HttpResponse<String> actualResponse = performGetRequest(BASE_URI + productId);
        String actualBody = actualResponse.body().replaceAll("\\r\\n|\\r|\\n", "\n");

        expectedBody = expectedBody.replaceAll("\\r\\n|\\r|\\n", "\n");

        assertEquals(200, actualResponse.statusCode());
        assertEquals(expectedBody, actualBody);
    }

    /**
     * Некорректный запрос не существующего товара.
     */
    @Test
    public void doGetExpectProductNotFoundExceptionStatusNotFound() throws Exception {
        long productId = 100L;
        String expectedBody = "{\"message\":\"Product by id = " + productId + " not found!\"}\n";
        expectedBody = expectedBody.replaceAll("\\r\\n|\\r|\\n", "\n");

        HttpResponse<String> actualResponse = performGetRequest(BASE_URI + productId);
        String actualBody = actualResponse.body().replaceAll("\\r\\n|\\r|\\n", "\n");


        assertEquals(404, actualResponse.statusCode());
        assertEquals(expectedBody, actualBody);
    }

    /**
     * Некорректный запрос, с некорректным Uri.
     */
    @Test
    public void doGetExpectIncorrectUriExceptionStatusNotFound() throws Exception {
        String incorrectPath = "sdfdf";
        String expectedBody = "{\"message\":\"Incorrect path: /" + incorrectPath + "\"}\n";
        expectedBody = expectedBody.replaceAll("\\r\\n|\\r|\\n", "\n");

        HttpResponse<String> actualResponse = performGetRequest(BASE_URI + incorrectPath);
        String actualBody = actualResponse.body().replaceAll("\\r\\n|\\r|\\n", "\n");

        assertEquals(404, actualResponse.statusCode());
        assertEquals(expectedBody, actualBody);
    }

    /**
     * Успешное изменение существующего товара.
     */
    @Test
    public void doPathExpectProductStatusOk() throws Exception {
        long productId = 14L;
        String requestBody = "{\"title\":\"Диск сцепления 184 (МАЗ Супер, Евро) d=51 мм.\",\"catalogueNumber\":\"184-1601130\",\"programNumber\":8256,\"technics\":[{\"title\":\"МАЗ\"}],\"balance\":15,\"price\":200.00}\n";

        HttpResponse<String> actualResponse = performPatchRequest(BASE_URI + productId, requestBody);
        String actualBody = actualResponse.body().replaceAll("\\r\\n|\\r|\\n", "\n");
        requestBody = requestBody.replaceAll("\\r\\n|\\r|\\n", "\n");

        assertEquals(200, actualResponse.statusCode());
        assertEquals(requestBody, actualBody);
    }

    /**
     * Некорректный запрос изменения товара, с некорректным Uri.
     */
    @Test
    public void doPathExpectIncorrectUriStatusNotFound() throws Exception {
        String incorrectPath = "sdfdf";
        String expectedBody = "{\"message\":\"Incorrect path: /" + incorrectPath + "\"}\n";

        HttpResponse<String> actualResponse = performPatchRequest(BASE_URI + incorrectPath, "");
        String actualBody = actualResponse.body().replaceAll("\\r\\n|\\r|\\n", "\n");
        expectedBody = expectedBody.replaceAll("\\r\\n|\\r|\\n", "\n");

        assertEquals(404, actualResponse.statusCode());
        assertEquals(expectedBody, actualBody);
    }

    /**
     * Некорректный запрос, попытка изменения не существующего товара.
     */
    @Test
    public void doPathExpectNoSuchProductNotFound() throws Exception {
        long productId = 100L;
        String requestBody = "{\"title\":\"Some title\"}";
        String expectedBody = "{\"message\":\"Product by id = " + productId + " not found!\"}\n";

        HttpResponse<String> actualResponse = performPatchRequest(BASE_URI + productId, requestBody);
        String actualBody = actualResponse.body().replaceAll("\\r\\n|\\r|\\n", "\n");
        expectedBody = expectedBody.replaceAll("\\r\\n|\\r|\\n", "\n");

        assertEquals(404, actualResponse.statusCode());
        assertEquals(expectedBody, actualBody);
    }

    /**
     * Некорректный запрос изменения товара, с ошибками валидации.
     */
    @Test
    public void doPathExpectValidationProductExceptionBadRequest() throws Exception {
        long productId = 1L;
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

        HttpResponse<String> actualResponse = performPatchRequest(BASE_URI + productId, requestBody);

        assertEquals(400, actualResponse.statusCode());
    }

    /**
     * Успешный запрос удаления товара.
     */
    @Test
    public void doDeleteExpectStatusOk() throws Exception {
        long productId = 100L;

        HttpResponse<String> actualResponse = performDeleteRequest(BASE_URI + productId);

        assertEquals(200, actualResponse.statusCode());
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
     * Служебный метод формирования Patch запроса.
     */
    private HttpResponse<String> performPatchRequest(String url, String body) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
    /**
     * Служебный метод формирования Delete запроса.
     */
    private HttpResponse<String> performDeleteRequest(String url) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

}
