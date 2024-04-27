package ru.maliutin.diesel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.maliutin.diesel.controller.ProductServlet;
import ru.maliutin.diesel.controller.ProductsServlet;
import ru.maliutin.diesel.dto.iMapperDto;
import ru.maliutin.diesel.repository.iProductRepository;
import ru.maliutin.diesel.service.ProductServiceImpl;
import ru.maliutin.diesel.utils.validation.iValidationService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InitServletsTest {

    @Test
    @SneakyThrows
    public void initProductsServletExpectCorrectInit(){
        ProductsServlet productsServlet = new ProductsServlet();
        ServletConfig config = mock(ServletConfig.class);
        when(config.getServletContext()).thenReturn(mock(ServletContext.class));
        when(config.getServletContext().getAttribute("productService"))
                .thenReturn(new ProductServiceImpl(
                        mock(iProductRepository.class),
                        mock(iMapperDto.class),
                        mock(iValidationService.class)));
        when(config.getServletContext().getAttribute("objectMapper"))
                .thenReturn(new ObjectMapper());
        productsServlet.init(config);

        assertEquals(ProductServiceImpl.class, getFieldClass(productsServlet, "productService")); // несколько ассертов нужно пихать в assertAll
        assertEquals(ObjectMapper.class, getFieldClass(productsServlet, "objectMapper"));
    }

    @Test
    @SneakyThrows
    public void initProductServletExpectCorrectInit(){
        ProductServlet productServlet = new ProductServlet();
        ServletConfig config = mock(ServletConfig.class);
        when(config.getServletContext()).thenReturn(mock(ServletContext.class));
        when(config.getServletContext().getAttribute("productService"))
                .thenReturn(new ProductServiceImpl(
                        mock(iProductRepository.class),
                        mock(iMapperDto.class),
                        mock(iValidationService.class)));
        when(config.getServletContext().getAttribute("objectMapper"))
                .thenReturn(new ObjectMapper());
        productServlet.init(config);

        assertEquals(ProductServiceImpl.class, getFieldClass(productServlet, "productService"));
        assertEquals(ObjectMapper.class, getFieldClass(productServlet, "objectMapper"));
    }

    private Object getFieldClass(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target).getClass();
    }

}
