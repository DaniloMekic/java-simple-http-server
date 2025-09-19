package com.danilomekic.http.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danilomekic.http.model.HttpRequest;
import com.danilomekic.http.util.HttpMessageUtil;

public class SimpleHttpRequestParser implements HttpRequestParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpRequestParser.class);

    @Override
    public HttpRequest parse(InputStream inputStream) throws IOException {
        String[] requestLine = parseRequestLine(inputStream);
        Map<String, List<String>> requestHeaders = parseRequestHeaders(inputStream);
        byte[] requestBody = parseRequestBody(inputStream);

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
                throw new BadRequestException("request-line not properly terminated with <CR><LF>");
            }

            if (HttpMessageUtil.isCharacterUSASCII(byteRead) == false) {
                throw new BadRequestException("request-line contains character that is not from US-ASCII character set");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseRequestHeaders'");
    }

    private byte[] parseRequestBody(InputStream inputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseRequestBody'");
    }
}
