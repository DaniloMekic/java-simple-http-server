package com.danilomekic.http.server.router;

import com.danilomekic.http.server.model.HttpRequest;
import com.danilomekic.http.server.model.HttpResponse;
import com.danilomekic.http.server.model.HttpVersion;
import com.danilomekic.http.server.model.Method;

public class HelloRoute implements Route {

    @Override
    public boolean matches(HttpRequest httpRequest) {
        return (httpRequest.requestMethod() == Method.GET) && (httpRequest.requestTarget().getPath().equals("/hello"));
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        byte[] httpResponseBody = "Hello from my simple HTTP server written in Java!!\n".getBytes();
        return new HttpResponse(HttpVersion.HTTP_1_1, 200, "OK", "text/plain", httpResponseBody);
    }
}
