package com.danilomekic.http.parser;

import java.io.IOException;
import java.io.InputStream;

import com.danilomekic.http.model.HttpRequest;

public interface HttpRequestParser {
    HttpRequest parse(InputStream inputStream) throws IOException;
}
