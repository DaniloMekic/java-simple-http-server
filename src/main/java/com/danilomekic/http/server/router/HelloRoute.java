package com.danilomekic.http.server.router;

import com.danilomekic.http.server.model.HttpRequest;
import com.danilomekic.http.server.model.HttpResponse;

public class HelloRoute implements Route {

    @Override
    public boolean matches(HttpRequest httpRequest) {
        return "GET".equals(httpRequest.requestMethod()) && "/hello".equals(httpRequest.requestTarget().getPath());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        byte[] httpResponseBody = "Hello from my simple HTTP server written in Java!!".getBytes();
        return new HttpResponse(200, "OK", "text/plain", httpResponseBody);
    }
}
