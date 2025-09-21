package com.danilomekic.http.serializer;

import java.io.IOException;

import com.danilomekic.http.model.HttpResponse;

public interface HttpResponseSerializer {
    byte[] serialize(HttpResponse httpResponse) throws IOException;
}
