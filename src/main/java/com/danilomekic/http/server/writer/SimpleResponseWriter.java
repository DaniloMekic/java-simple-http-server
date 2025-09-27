package com.danilomekic.http.server.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danilomekic.http.server.model.HttpResponse;

public class SimpleResponseWriter implements ResponseWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleResponseWriter.class);

    @Override
    public void write(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        try {
            StringBuilder httpResponseBuilder = new StringBuilder();

            httpResponseBuilder
                .append(httpResponse.httpVersion().toString())
                .append("\s")
                .append(httpResponse.statusCode())
                .append("\s")
                .append(httpResponse.reasonPhrase())
                .append("\r\n");

            // Content-Type header
            httpResponseBuilder
                .append("Content-Type: ")
                .append(httpResponse.contentType())
                .append("\r\n");

            // Content-Length
            byte[] responseBody = httpResponse.responseBody();
            int contentLength = (responseBody != null) ? responseBody.length : 0;
            httpResponseBuilder
                .append("Content-Length: ")
                .append(contentLength)
                .append("\r\n");

            // Connection header
            httpResponseBuilder
                .append("Connection: close")
                .append("\r\n");

            // Empty line separating headers from the body
            httpResponseBuilder
                .append("\r\n");

            byte[] headerBytes = httpResponseBuilder.toString().getBytes();
            outputStream.write(headerBytes);

            // Write response body if present
            if (responseBody != null && responseBody.length > 0) {
                outputStream.write(responseBody);
            }

            outputStream.flush();

            LOGGER.debug("HTTP Response written successfully");
        } catch (SocketException e) {
            LOGGER.error("Client disconnected while writing response: {}", e);
        } catch (IOException e) {
            LOGGER.error("IO Error while writing HTTP response: {}", e);
            throw e;
        }
    }
}
