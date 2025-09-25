package com.danilomekic.http.server.router;

import java.util.Optional;

import com.danilomekic.http.server.model.HttpRequest;

public interface Router {
    void addRoute(Route route);
    Optional<Route> findMatchingRoute(HttpRequest httpRequest);
}
