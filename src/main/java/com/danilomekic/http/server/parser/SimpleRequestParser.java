package com.danilomekic.http.server.parser;

import com.danilomekic.http.server.exception.BadRequestException;
import com.danilomekic.http.server.exception.HttpVersionNotSupportedException;
import com.danilomekic.http.server.model.HttpRequest;
import com.danilomekic.http.server.model.HttpVersion;
import com.danilomekic.http.server.model.Method;
import com.danilomekic.http.server.util.HttpMessageUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleRequestParser implements RequestParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRequestParser.class);
    private static final int CR = 13;
    private static final int LF = 10;

    String[] requestLine;
    Map<String, List<String>> requestHeaders;
    byte[] requestBody;

    @Override
    public HttpRequest parse(InputStream inputStream) throws IOException, URISyntaxException {
        parseRequestLine(inputStream);
        parseRequestHeaders(inputStream);
        parseRequestBody(inputStream);

        return new HttpRequest(
                Enum.valueOf(Method.class, requestLine[0]), // Method
                new URI(requestLine[1]), // Target
                HttpVersion.fromString(requestLine[2]), // HTTP version
                requestHeaders, // Headers
                requestBody // Body
                );
    }

    // Request-line = Method <Space> Target <Space> HTTP-version
    private void parseRequestLine(InputStream inputStream) throws IOException {
        StringBuilder requestLineBuilder = new StringBuilder();
        boolean previousCharWasCR = false;
        int byteRead = -1;

        LOGGER.info("Parsing request-line from the stream");

        while ((byteRead = inputStream.read()) != -1) {
            // Proper line ending: CRLF
            if (previousCharWasCR && byteRead == LF) {
                break; // End of request line
            }

            // Incorrect line ending: LF not preceded by CR
            if (byteRead == LF && !previousCharWasCR) {
                throw new BadRequestException("Request-line not properly terminated with <CR><LF>");
            }

            if (HttpMessageUtil.isCharacterUSASCII(byteRead) == false) {
                throw new BadRequestException(
                        "Request-line contains character that is not from US-ASCII character set");
            }

            if (byteRead == CR) {
                previousCharWasCR = true;
                continue; // Don't append <CR> to the string; next iteration should check if it's
                // followed by <LF>
            }

            if (previousCharWasCR) {
                throw new BadRequestException("Request-line contains <CR> not followed by <LF>");
            }

            requestLineBuilder.appendCodePoint(byteRead);
        }

        // Check for end of stream without proper line ending
        if (byteRead == -1) {
            throw new BadRequestException("Unexpected end of stream while parsing request-line");
        }

        this.requestLine = requestLineBuilder.toString().split("\\s", 3);

        if (this.requestLine.length != 3) {
            throw new BadRequestException(
                    "Invalid request-line structure. Should be tripartite: <Method> <Target>"
                            + " <Version>");
        }

        if (HttpMessageUtil.isValidMethod(this.requestLine[0]) == false) {
            throw new BadRequestException("Invalid request method name");
        }

        try {
            HttpVersion.fromString(this.requestLine[2]);
        } catch (IllegalArgumentException e) {
            throw new HttpVersionNotSupportedException(this.requestLine[2]);
        }
    }

    private void parseRequestHeaders(InputStream inputStream) throws IOException {
        this.requestHeaders = new HashMap<>();

        String fieldLine;
        String fieldName;
        String fieldValue;
        int colonIndex = -1;

        while (true) {
            fieldLine = parseFieldLine(inputStream);

            if (fieldLine == null) { // End of headers
                break;
            }

            colonIndex = fieldLine.indexOf(':');
            if (colonIndex == -1) {
                throw new BadRequestException("HTTP header does not contain colon character");
            }

            fieldName = fieldLine.substring(0, colonIndex).trim().toLowerCase();
            if (HttpMessageUtil.isValidToken(fieldName) == false) {
                throw new IllegalArgumentException("Invalid HTTP header name");
            }

            fieldValue = fieldLine.substring(colonIndex + 1).trim();

            this.requestHeaders
                    .computeIfAbsent(fieldName, key -> new ArrayList<>())
                    .addAll(HttpMessageUtil.getFieldValuesList(fieldValue));
        }
    }

    private String parseFieldLine(InputStream inputStream) throws IOException {
        StringBuilder lineFromStream = new StringBuilder();
        boolean previousCharWasCR = false;
        int byteRead;

        while ((byteRead = inputStream.read()) != -1) {
            // Proper line ending: CRLF
            if (previousCharWasCR && byteRead == LF) {
                break;
            }

            // Check for <LF> without <CR>
            if (byteRead == LF && !previousCharWasCR) {
                break;
            }

            // CR
            if (byteRead == CR) {
                previousCharWasCR = true;
                continue; // Don't append <CR>
            }

            // CR not followed by LF
            if (previousCharWasCR) {
                lineFromStream.appendCodePoint(13); // Append previous <CR>
                previousCharWasCR = false;
            }

            lineFromStream.appendCodePoint(byteRead);
        }

        return lineFromStream.length() == 0
                        || byteRead == -1 // Line is empty or stream has ended (message has no body)
                ? null
                : lineFromStream.toString();
    }

    private void parseRequestBody(InputStream inputStream) throws IOException {
        List<String> contentLengthHeaders = requestHeaders.get("content-length");

        if (contentLengthHeaders == null || contentLengthHeaders.isEmpty()) {
            LOGGER.info("No 'Content-Length' header found. Assuming no body");
            this.requestBody = new byte[0]; // Empty body
            return;
        }

        try {
            int contentLength = Integer.parseInt(contentLengthHeaders.getFirst());

            if (contentLength < 0) {
                throw new BadRequestException("Content-Length cannot be negative");
            }

            if (contentLength == 0) {
                this.requestBody = new byte[0];
                return;
            }

            this.requestBody = new byte[contentLength];
            int totalBytesRead = 0;

            // Read exactly Content-Length bytes
            while (totalBytesRead < contentLength) {
                int bytesRead =
                        inputStream.read(
                                this.requestBody, totalBytesRead, contentLength - totalBytesRead);
                if (bytesRead == -1) {
                    throw new BadRequestException(
                            "Unexpected end of stream while reading request body");
                }
                totalBytesRead += bytesRead;
            }

            LOGGER.debug("Successfully read {} bytes of request body", totalBytesRead);

        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid Content-Length header value");
        }
    }
}
