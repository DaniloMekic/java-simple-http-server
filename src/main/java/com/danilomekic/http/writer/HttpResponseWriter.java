package com.danilomekic.http.writer;

import java.io.IOException;
import java.io.OutputStream;

import com.danilomekic.http.model.HttpResponse;

public interface HttpResponseWriter {
    void write(HttpResponse httpResponse, OutputStream outputStream) throws IOException;
}
