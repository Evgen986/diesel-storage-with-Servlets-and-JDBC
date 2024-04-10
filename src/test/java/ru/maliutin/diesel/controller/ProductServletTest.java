package ru.maliutin.diesel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.maliutin.diesel.dto.ProductDTO;
import ru.maliutin.diesel.exception.ExceptionBody;
import ru.maliutin.diesel.exception.NoSuchProductException;
import ru.maliutin.diesel.exception.ValidationProductException;
import ru.maliutin.diesel.service.iProductService;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServletTest {

    @Mock
    private iProductService productService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ProductServlet productServlet;

    @BeforeEach
    @SneakyThrows
    public void prepare(){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    // region doGet

    @Test
    @SneakyThrows
    public void doGetExpectProductStatusOK(){
        long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("Test product");
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(productService.getProductById(productId)).thenReturn(productDTO);

        productServlet.doGet(request, response);

        verify(productService).getProductById(productId);
        verify(request).getPathInfo();
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValueAsString(productDTO);
    }

    @Test
    @SneakyThrows
    public void doGetExpectNoSuchProductExceptionStatusNotFound(){
        long productId = 1L;
        when(request.getPathInfo()).thenReturn("/" + productId);
        doThrow(new NoSuchProductException("exception message"))
                .when(productService).getProductById(productId);

        productServlet.doGet(request, response);

        verify(productService).getProductById(productId);
        verify(request).getPathInfo();
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(objectMapper).writeValueAsString(any(ExceptionBody.class));
    }

    @Test
    @SneakyThrows
    public void doGetExpectSQLExceptionStatusServerError(){
        long productId = 1L;
        when(request.getPathInfo()).thenReturn("/" + productId);
        doThrow(new SQLException())
                .when(productService).getProductById(productId);

        productServlet.doGet(request, response);

        verify(productService).getProductById(productId);
        verify(request).getPathInfo();
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @SneakyThrows
    public void doGetExceptIncorrectUriException(){
        when(request.getPathInfo()).thenReturn("incorrect path");

        productServlet.doGet(request, response);

        verify(request).getPathInfo();
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(objectMapper).writeValueAsString(any(ExceptionBody.class));
    }

    // endregion

    // region doPath
    @Test
    @SneakyThrows
    public void doPathExpectProductStatusOK(){
        long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("Test product");

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContentType("application/json; charset=UTF-8");
        req.setContent(convertProductDto(productDTO));
        req.setPathInfo("/" + productId);
        request = req;
        when(objectMapper.readValue(any(String.class), eq(ProductDTO.class))).thenReturn(productDTO);
        when(productService.editProduct(productId, productDTO)).thenReturn(productDTO);

        productServlet.doPatch(request, response);

        verify(productService).editProduct(productId, productDTO);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValueAsString(productDTO);
    }

    @Test
    @SneakyThrows
    public void doPathExpectIncorrectUriExceptionStatusNotFound(){
        when(request.getPathInfo()).thenReturn("incorrect path");

        productServlet.doPatch(request, response);

        verify(productService, never()).editProduct(any(Long.class), any(ProductDTO.class));
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(objectMapper).writeValueAsString(any(ExceptionBody.class));
    }

    @Test
    @SneakyThrows
    public void doPathExpectNoSuchProductExceptionStatusNotFound(){
        long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("Test product");

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContentType("application/json; charset=UTF-8");
        req.setContent(convertProductDto(productDTO));
        req.setPathInfo("/" + productId);
        request = req;
        when(objectMapper.readValue(any(String.class), eq(ProductDTO.class))).thenReturn(productDTO);
        doThrow(new NoSuchProductException("No such product exception"))
                .when(productService).editProduct(productId, productDTO);

        productServlet.doPatch(request, response);

        verify(productService).editProduct(productId, productDTO);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(objectMapper).writeValueAsString(any(ExceptionBody.class));
    }

    @Test
    @SneakyThrows
    public void doPathExpectValidationProductExceptionStatusBadRequest(){
        long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("Test product");

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContentType("application/json; charset=UTF-8");
        req.setContent(convertProductDto(productDTO));
        req.setPathInfo("/" + productId);
        request = req;
        when(objectMapper.readValue(any(String.class), eq(ProductDTO.class))).thenReturn(productDTO);
        doThrow(new ValidationProductException("Validation exception", List.of("messages exception")))
                .when(productService).editProduct(productId, productDTO);

        productServlet.doPatch(request, response);

        verify(productService).editProduct(productId, productDTO);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @SneakyThrows
    public void doPathExpectSqlExceptionStatusServerError(){
        long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("Test product");

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContentType("application/json; charset=UTF-8");
        req.setContent(convertProductDto(productDTO));
        req.setPathInfo("/" + productId);
        request = req;
        when(objectMapper.readValue(any(String.class), eq(ProductDTO.class))).thenReturn(productDTO);
        doThrow(new SQLException("SQL exception"))
                .when(productService).editProduct(productId, productDTO);

        productServlet.doPatch(request, response);

        verify(productService).editProduct(productId, productDTO);
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    // endregion

    // region doDelete

    @Test
    @SneakyThrows
    public void doDeleteExpectStatusOk(){
        long productId = 1L;
        when(request.getPathInfo()).thenReturn("/" + productId);
        doNothing().when(productService).deleteProduct(productId);

        productServlet.doDelete(request, response);

        verify(productService).deleteProduct(productId);
        verify(request).getPathInfo();
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @SneakyThrows
    public void doDeleteExpectSQLExceptionStatusServerError(){
        long productId = 1L;
        when(request.getPathInfo()).thenReturn("/" + productId);
        doThrow(new SQLException())
                .when(productService).deleteProduct(productId);

        productServlet.doDelete(request, response);

        verify(productService).deleteProduct(productId);
        verify(request).getPathInfo();
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @SneakyThrows
    public void doDeleteExceptIncorrectUriException(){
        when(request.getPathInfo()).thenReturn("incorrect path");

        productServlet.doDelete(request, response);

        verify(request).getPathInfo();
        verify(response).setContentType("application/json; charset=UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(objectMapper).writeValueAsString(any(ExceptionBody.class));
    }

    // endregion

    @SneakyThrows
    private byte[] convertProductDto(ProductDTO productDTO){
        byte[] byteArray;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(productDTO);
            byteArray = bos.toByteArray();
        }
        return byteArray;
    }
}
