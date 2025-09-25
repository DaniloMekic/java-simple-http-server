package com.danilomekic.http.server.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import com.danilomekic.http.server.model.HttpRequest;

public interface HttpRequestParser {
    HttpRequest parse(InputStream inputStream) throws IOException, URISyntaxException;
}
