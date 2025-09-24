package com.danilomekic.http.server.writer;

import java.io.IOException;
import java.io.OutputStream;

import com.danilomekic.http.server.model.HttpResponse;

public interface HttpResponseWriter {
    void write(HttpResponse httpResponse, OutputStream outputStream) throws IOException;
}
