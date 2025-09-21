package com.danilomekic.http.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danilomekic.http.model.HttpResponse;
import com.danilomekic.http.serializer.HttpResponseSerializer;

public class SimpleHttpResponseWriter implements HttpResponseWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpResponseWriter.class);
    private final HttpResponseSerializer serializer;

    public SimpleHttpResponseWriter(HttpResponseSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void write(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        byte[] httpResponseBytes = serializer.serialize(httpResponse);
        LOGGER.info("Writing response bytes to output stream");
        outputStream.write(httpResponseBytes);
        LOGGER.info("Flushing output stream");
        outputStream.flush();
    }
}
