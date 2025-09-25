package com.danilomekic.http.server.router;

import com.danilomekic.http.server.model.HttpRequest;
import com.danilomekic.http.server.model.HttpResponse;

public interface Route {
    boolean matches(HttpRequest httpRequest);
    void handle(HttpRequest httpRequest, HttpResponse httpResponse);
}
