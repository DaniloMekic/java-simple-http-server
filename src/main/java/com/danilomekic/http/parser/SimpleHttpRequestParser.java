package com.danilomekic.http.parser;

import java.io.IOException;
import java.io.InputStream;
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
        parseRequestLine(inputStream);
        parseRequestHeaders(inputStream);
        parseRequestBody(inputStream);

        return new HttpRequest(requestLine[0], requestLine[1], requestLine[2], requestHeaders, requestBody);
    }

    private void parseRequestLine(InputStream inputStream) {
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

        this.requestLine = requestLineBuilder.toString().split("\s", 3);

        if (HttpMessageUtil.isValidMethod(this.requestLine[0]) == false) {
            throw new BadRequestException("Invalid request method name");
        }
    }

    private void parseRequestHeaders(InputStream inputStream) {
        this.requestHeaders = new HashMap<>();

        String fieldLine;
        String fieldName;
        String fieldValue;
        int colonIndex = -1;

        try {
            while (true) {
                fieldLine = parseFieldLine(inputStream);
                LOGGER.debug("Field line: {}", fieldLine);

                if (fieldLine == null || fieldLine.isEmpty()) {
                    break; // End of headers
                }

                colonIndex = fieldLine.indexOf(':');
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
                    .trim();

                this.requestHeaders
                    .computeIfAbsent(fieldName, key -> new ArrayList<>())
                    .addAll(HttpMessageUtil.getFieldValuesList(fieldValue));
            }
            
        } catch (Exception e) {
            LOGGER.error("Failed to read header line: {}", e);
        }
    }

    private String parseFieldLine(InputStream inputStream) throws IOException {
        StringBuilder lineFromStream = new StringBuilder();
        boolean previousChatWasCR = false;
        int byteRead;

        while ((byteRead = inputStream.read()) != -1) {
            if (byteRead == 10 && previousChatWasCR) { // LF after CR
                break;
            }

            // CR
            if (byteRead == 13) {
                previousChatWasCR = true;
                continue;
            }

            // CR not folowed by LF
            if (previousChatWasCR) {
                lineFromStream.append((char) 13);
                previousChatWasCR = false;
            }

            lineFromStream.append((char) byteRead);
        }

        return lineFromStream.length() == 0 && byteRead == -1
            ? null
            : lineFromStream.toString();
        
    }

    private void parseRequestBody(InputStream inputStream) {
        int contentLength = Integer.parseInt(requestHeaders.get("content-length").getFirst());

        try {
            inputStream.read(this.requestBody, 0, contentLength);
        } catch (Exception e) {
            LOGGER.error("Failed to read body bytes: {}", e);
        }

        try {
            inputStream.close();
        } catch (Exception e) {
            LOGGER.error("Failed to close stream for body bytes: {}", e);
        }
    }
}
