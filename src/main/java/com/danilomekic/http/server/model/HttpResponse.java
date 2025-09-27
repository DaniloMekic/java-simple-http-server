package com.danilomekic.http.server.model;

public record HttpResponse(HttpVersion httpVersion, int statusCode, String reasonPhrase, String contentType, byte[] responseBody) {}
