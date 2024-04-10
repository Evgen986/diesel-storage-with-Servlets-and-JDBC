package ru.maliutin.diesel.controller;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Интерфейс получения тела запроса.
 */
public interface ReadBodyRequest {

    default String readBody(HttpServletRequest req) throws IOException {
        InputStream inputStream = req.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null){
            requestBody.append(line);
        }
        return requestBody.toString();
    }

}
