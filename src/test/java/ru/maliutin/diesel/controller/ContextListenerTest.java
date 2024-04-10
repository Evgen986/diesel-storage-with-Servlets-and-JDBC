package ru.maliutin.diesel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.maliutin.diesel.service.ProductServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ContextListenerTest {

    @Test
    public void contextInitialized(){
        ServletContextEvent servletContextEventMock = mock(ServletContextEvent.class);
        ServletContext servletContext = mock(ServletContext.class);

        ContextListener contextListener = new ContextListener();
        Mockito.when(servletContextEventMock.getServletContext()).thenReturn(servletContext);

        contextListener.contextInitialized(servletContextEventMock);

        Mockito.verify(servletContext).setAttribute(eq("productService"), any(ProductServiceImpl.class));
        Mockito.verify(servletContext).setAttribute(eq("objectMapper"), any(ObjectMapper.class));
    }

}
