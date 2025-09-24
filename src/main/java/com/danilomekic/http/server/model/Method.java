package com.danilomekic.http.server.model;

/*
* RFC 9110: Section 9
*/
public enum Method {
    GET(true),
    HEAD(true),
    POST(false),
    PUT(false),
    DELETE(false),
    CONNECT(false),
    OPTIONS(true),
    TRACE(true),
    PATCH(false);

    private final boolean isSafe;

    private Method(boolean isSafe) {
        this.isSafe = isSafe;
    }

    public boolean isSafe() {
        return isSafe;
    }
}
