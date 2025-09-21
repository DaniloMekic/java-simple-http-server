package com.danilomekic.http.model;

public record HttpResponse(int statusCode, String reasonPhrase, String contentType, byte[] responseBody) {}
