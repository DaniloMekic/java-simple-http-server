package com.danilomekic.http.server.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.danilomekic.http.server.model.HttpRequest;

public class SimpleRouter implements Router {
    private final List<Route> requestRoutes = new ArrayList<>();

    @Override
    public void addRoute(Route newRoute) {
        requestRoutes.add(newRoute);
    }

    @Override
    public Optional<Route> findMatchingRoute(HttpRequest httpRequest) {
        return requestRoutes
            .stream()
            .filter(route -> route.matches(httpRequest))
            .findFirst();
    }
}
