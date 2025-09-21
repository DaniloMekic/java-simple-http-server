package com.danilomekic.http.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danilomekic.http.model.HttpRequest;
import com.danilomekic.http.model.HttpResponse;
import com.danilomekic.http.parser.HttpRequestParser;
import com.danilomekic.http.parser.SimpleHttpRequestParser;
import com.danilomekic.http.serializer.SimpleHttpResponseSerializer;
import com.danilomekic.http.writer.HttpResponseWriter;
import com.danilomekic.http.writer.SimpleHttpResponseWriter;

public class HttpConnectionHandler implements ConnectionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionHandler.class);

    private final HttpRequestParser httpRequestParser = new SimpleHttpRequestParser();
    private final HttpResponseWriter httpResponseWriter = new SimpleHttpResponseWriter(new SimpleHttpResponseSerializer());

    @Override
    public void handle(Socket socket) {
        try (
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            ) {
            LOGGER.info("Parsing HTTP request");

            HttpRequest httpRequest = httpRequestParser.parse(inputStream);

            HttpResponse httpResponse = new HttpResponse(200, "OK", "plain/text", null);

            httpResponseWriter.write(httpResponse, outputStream);
        } catch (Exception e) {
            LOGGER.error("Error handling connection: {}", e);
        }
    }
}
