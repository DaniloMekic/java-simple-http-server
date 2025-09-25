package com.danilomekic.http.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danilomekic.http.server.model.HttpRequest;
import com.danilomekic.http.server.model.HttpResponse;
import com.danilomekic.http.server.parser.HttpRequestParser;
import com.danilomekic.http.server.writer.HttpResponseWriter;

public class HttpConnectionHandler implements ConnectionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionHandler.class);

    private final HttpRequestParser httpRequestParser;
    private final HttpResponseWriter httpResponseWriter;

    public HttpConnectionHandler(HttpRequestParser httpRequestParser, HttpResponseWriter httpResponseWriter) {
        this.httpRequestParser = httpRequestParser;
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

            HttpResponse httpResponse = new HttpResponse(200, "OK", "plain/text", null);

            httpResponseWriter.write(httpResponse, socketOutputStream);
        } catch (Exception e) {
            LOGGER.error("Error handling connection: {}", e);
        }
    }
}
