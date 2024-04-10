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
import ru.maliutin.diesel.exception.ValidationProductException;
import ru.maliutin.diesel.service.iProductService;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductsServletTest {

    @Mock
    private iProductService productService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ProductsServlet productsServlet;

    @BeforeEach
    @SneakyThrows
    public void prepare(){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @SneakyThrows
    public void doGetExpectListProductsStatusOk(){
        List<ProductDTO> products = List.of(new ProductDTO());
        when(productService.getAllProduct()).thenReturn(products);

        productsServlet.doGet(request, response);

        verify(productService).getAllProduct();
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @SneakyThrows
    public void doGetExpectSQLExceptionStatusServerError(){

        doThrow(new SQLException()).when(productService).getAllProduct();

        productsServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @SneakyThrows
    public void doPostExceptProductStatusOk(){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("test product");

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContentType("application/json; charset=UTF-8");
        req.setContent(convertProductDto(productDTO));
        request = req;

        when(objectMapper.readValue(anyString(), eq(ProductDTO.class)))
                .thenReturn(productDTO);
        when(productService.addProduct(productDTO)).thenReturn(productDTO);

        productsServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValueAsString(productDTO);
    }

    @Test
    @SneakyThrows
    public void doPostExceptValidationProductExceptionStatusBadRequest(){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("test product");

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContentType("application/json; charset=UTF-8");
        req.setContent(convertProductDto(productDTO));
        request = req;

        when(objectMapper.readValue(anyString(), eq(ProductDTO.class)))
                .thenReturn(productDTO);
        doThrow(
                new ValidationProductException("validation exception",
                        List.of("exception message")))
                .when(productService).addProduct(productDTO);

        productsServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(objectMapper).writeValueAsString(anyString());
        verify(objectMapper).writeValueAsString(any(List.class));
    }

    @Test
    @SneakyThrows
    public void doPostExceptSqlExceptionStatusServerError(){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("test product");

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContentType("application/json; charset=UTF-8");
        req.setContent(convertProductDto(productDTO));
        request = req;

        when(objectMapper.readValue(anyString(), eq(ProductDTO.class)))
                .thenReturn(productDTO);
        doThrow(
                new SQLException("sql exception"))
                .when(productService).addProduct(productDTO);

        productsServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

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
