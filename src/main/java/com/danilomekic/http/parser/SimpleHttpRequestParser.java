package com.danilomekic.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danilomekic.http.model.HttpRequest;
import com.danilomekic.http.util.HttpMessageUtil;

public class SimpleHttpRequestParser implements HttpRequestParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpRequestParser.class);

    String[] requestLine;
    Map<String, List<String>> requestHeaders;
    byte[] requestBody;

    @Override
    public HttpRequest parse(InputStream inputStream) throws IOException {
        requestLine = parseRequestLine(inputStream);
        requestHeaders = parseRequestHeaders(inputStream);
        requestBody = parseRequestBody(inputStream);

        return new HttpRequest(requestLine[0], requestLine[1], requestLine[2], requestHeaders, requestBody);
    }

    private String[] parseRequestLine(InputStream inputStream) {
        String[] requestLine;
        StringBuilder requestLineBuilder = new StringBuilder();
        boolean isPreviousCharacterCR = false;
        int byteRead = -1;

        LOGGER.info("Parsing request-line from the stream");
        while (byteRead != 10 && isPreviousCharacterCR == false) {
            try {
                byteRead = inputStream.read();
            } catch (Exception e) {
                LOGGER.error("Failed to read byte from request-line stream: {}", e);
            }

            // Check if <LF> is preceded by <CR>
            if (byteRead == 10 && isPreviousCharacterCR == false) {
                throw new BadRequestException("Request-line not properly terminated with <CR><LF>");
            }

            if (HttpMessageUtil.isCharacterUSASCII(byteRead) == false) {
                throw new BadRequestException("Request-line contains character that is not from US-ASCII character set");
            }

            if (byteRead == 13) {
                isPreviousCharacterCR = true;
                continue;
            }

            requestLineBuilder.appendCodePoint(byteRead);
        }

        requestLine = requestLineBuilder.toString().split("\s", 3);

        if (HttpMessageUtil.isValidMethod(requestLine[0])) {
            return requestLine;
        } else {
            throw new BadRequestException("Invalid request method name");
        }
    }

    private Map<String, List<String>> parseRequestHeaders(InputStream inputStream) {
        Map<String, List<String>> requestHeaders = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String fieldLine;
        String fieldName;
        String fieldValue;

        try {
            while ((fieldLine = bufferedReader.readLine()) != null) {
                if (fieldLine.isEmpty()) {
                    break; // End of headers
                }

                int colonIndex = fieldLine.indexOf(':');
                if (colonIndex == -1) {
                    throw new BadRequestException("HTTP header does not contain colon character");
                }

                fieldName = fieldLine
                    .substring(0, colonIndex)
                    .trim()
                    .toLowerCase();
                if (HttpMessageUtil.isValidToken(fieldName)== false) {
                    throw new IllegalArgumentException("Invalid HTTP header name");
                }

                fieldValue = fieldLine
                    .substring(colonIndex + 1)
                    .trim()
                    .toLowerCase();

                requestHeaders
                    .computeIfAbsent(fieldName, key -> new ArrayList<>())
                    .addAll(HttpMessageUtil.getFieldValuesList(fieldValue));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read header line: {}", e);
        }

        try {
            bufferedReader.close();
        } catch (Exception e) {
            LOGGER.error("Failed to close buffered reader for headers: {}", e);
        }

        return requestHeaders;
    }

    private byte[] parseRequestBody(InputStream inputStream) {
        byte[] bodyBytes = null;
        int contentLength = Integer.parseInt(requestHeaders.get("content-length").getFirst());

        try {
            inputStream.read(bodyBytes, 0, contentLength);
        } catch (Exception e) {
            LOGGER.error("Failed to read body bytes: {}", e);
        }

        try {
            inputStream.close();
        } catch (Exception e) {
            LOGGER.error("Failed to close stream for body bytes: {}", e);
        }

        return bodyBytes;
    }
}
