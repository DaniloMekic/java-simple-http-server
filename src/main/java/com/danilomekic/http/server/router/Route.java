package com.danilomekic.http.server.router;

import com.danilomekic.http.server.model.HttpRequest;
import com.danilomekic.http.server.model.HttpResponse;
import com.danilomekic.http.server.model.Method;

import java.util.Set;

public interface Route {
    boolean matches(HttpRequest httpRequest);

    HttpResponse handle(HttpRequest httpRequest);

    boolean matchesPath(HttpRequest httpRequest);

    Set<Method> getSupportedMethods();
}
