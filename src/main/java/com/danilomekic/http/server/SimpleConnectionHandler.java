package com.danilomekic.http.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danilomekic.http.server.model.HttpRequest;
import com.danilomekic.http.server.model.HttpResponse;
import com.danilomekic.http.server.parser.RequestParser;
import com.danilomekic.http.server.router.Router;
import com.danilomekic.http.server.writer.ResponseWriter;

public class SimpleConnectionHandler implements ConnectionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConnectionHandler.class);

    private final RequestParser httpRequestParser;
    private final Router httpRequestRouter;
    private final ResponseWriter httpResponseWriter;

    public SimpleConnectionHandler(RequestParser httpRequestParser, Router httpRequestRouter, ResponseWriter httpResponseWriter) {
        this.httpRequestParser = httpRequestParser;
        this.httpRequestRouter = httpRequestRouter;
        this.httpResponseWriter = httpResponseWriter;
    }

    @Override
    public void handle(Socket socket) {
        try (
            InputStream socketInputStream = socket.getInputStream();
            OutputStream socketOutputStream = socket.getOutputStream();
            ) {
            LOGGER.info("Parsing HTTP request");

            HttpRequest httpRequest = httpRequestParser.parse(socketInputStream);

            HttpResponse httpResponse = httpRequestRouter
                .findMatchingRoute(httpRequest)
                .map(route -> route.handle(httpRequest))
                .orElse(new HttpResponse(404, "Not Found", "text/plain", "404 Not Found".getBytes()));

            httpResponseWriter.write(httpResponse, socketOutputStream);
        } catch (Exception e) {
            LOGGER.error("Error handling connection: {}", e);
        }
    }
}
