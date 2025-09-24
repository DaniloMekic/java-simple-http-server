package com.danilomekic.http.server.model;

public record HttpResponse(int statusCode, String reasonPhrase, String contentType, byte[] responseBody) {}
