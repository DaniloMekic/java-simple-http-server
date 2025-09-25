package com.danilomekic.http.server.router;

import com.danilomekic.http.server.model.HttpRequest;
import com.danilomekic.http.server.model.HttpResponse;

public interface Route {
    boolean matches(HttpRequest httpRequest);
    HttpResponse handle(HttpRequest httpRequest);
}
