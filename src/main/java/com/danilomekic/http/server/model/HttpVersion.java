package com.danilomekic.http.server.model;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2"),
    HTTP_3("HTTP/3");

    private final String protocolVersionString;

    private HttpVersion(String protocolVersionString) {
        this.protocolVersionString = protocolVersionString;
    }

    @Override
    public String toString() {
        return protocolVersionString;
    }

    public static HttpVersion fromString(String value) {
        if (value  != null) {
            for (HttpVersion httpVersionEnum : HttpVersion.values()) {
                if (httpVersionEnum.protocolVersionString.equals(value)) {
                    return httpVersionEnum;
                }
            }
        }

        throw new IllegalArgumentException("Unknown HTTP protocol version: " + value);
    }
}
