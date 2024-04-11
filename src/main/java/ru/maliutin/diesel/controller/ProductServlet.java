package ru.maliutin.diesel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.maliutin.diesel.dto.ProductDTO;
import ru.maliutin.diesel.exception.ExceptionBody;
import ru.maliutin.diesel.exception.IncorrectUriException;
import ru.maliutin.diesel.exception.NoSuchProductException;
import ru.maliutin.diesel.exception.ValidationProductException;
import ru.maliutin.diesel.service.ProductServiceImpl;
import ru.maliutin.diesel.service.iProductService;

import java.io.*;
import java.sql.SQLException;

/**
 * Сервлет обработки запросов с идентификатором товара.
 */
@WebServlet(urlPatterns = "/product/*")
public class ProductServlet extends HttpServlet implements ReadBodyRequest {

    private iProductService productService;
    private ObjectMapper objectMapper;

    /**
     * Инициализация сервлета.
     */
    @Override
    public void init(ServletConfig config) {
        ServletContext servletContext = config.getServletContext();

        final Object productService = servletContext.getAttribute("productService");
        this.productService = (ProductServiceImpl) productService;
        final Object objectMapper = servletContext.getAttribute("objectMapper");
        this.objectMapper = (ObjectMapper) objectMapper;
    }

    /**
     * Подключение возможности использования метода PATCH.
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getMethod().equals("PATCH")){
            doPatch(req, resp);
        } else if (req.getMethod().equals("DELETE")) {
            doDelete(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    /**
     * Получение товара по id
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter pw = resp.getWriter();
        resp.setContentType("application/json; charset=UTF-8");
        try {
            ProductDTO productDTO = productService.getProductById(getIdFromPath(req.getPathInfo()));
            resp.setStatus(200);
            pw.println(objectMapper.writeValueAsString(productDTO));
        }catch (IncorrectUriException | NoSuchProductException e){
            resp.setStatus(404);
            pw.println(objectMapper.writeValueAsString(new ExceptionBody(e.getMessage())));
        }catch (SQLException e){
            resp.setStatus(500);
            pw.println(e.getMessage());
        }
    }

    /**
     * Изменение товара по id
     */
    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter pw = resp.getWriter();
        resp.setContentType("application/json; charset=UTF-8");
        try {
            long productId = getIdFromPath(req.getPathInfo());
            ProductDTO productDTO = objectMapper.readValue(readBody(req), ProductDTO.class);
            productDTO = productService.editProduct(productId, productDTO);
            resp.setStatus(200);
            pw.println(objectMapper.writeValueAsString(productDTO));
        }catch (IncorrectUriException | NoSuchProductException e){
            resp.setStatus(404);
            pw.println(objectMapper.writeValueAsString(new ExceptionBody(e.getMessage())));
        } catch (ValidationProductException e){
            resp.setStatus(400);
            pw.println(e.getMessage());
            pw.println(e.getMessages());
        }catch (SQLException e){
            resp.setStatus(500);
            pw.println(e.getMessage());
        }
    }

    /**
     * Удаление товара по id
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter pw = resp.getWriter();
        try {
            long productId = getIdFromPath(req.getPathInfo());
            productService.deleteProduct(productId);
            resp.setStatus(200);
        }catch (IncorrectUriException e){
            resp.setContentType("application/json; charset=UTF-8");
            resp.setStatus(404);
            pw.println(objectMapper.writeValueAsString(new ExceptionBody(e.getMessage())));
        }catch (SQLException e){
            resp.setStatus(500);
            pw.println(e.getMessage());
        }
    }

    /**
     * Служебный метод получения идентификатора из Uri пути.
     * @param path Uri путь.
     * @return long уникальный идентификатор.
     * @exception IncorrectUriException исключение некорректного пути.
     */
    private long getIdFromPath(String path) throws IncorrectUriException{
        try{
            return Long.parseLong(path.substring(1));
        } catch (NumberFormatException e){
            throw new IncorrectUriException("Incorrect path: " + path);
        }
    }
}
