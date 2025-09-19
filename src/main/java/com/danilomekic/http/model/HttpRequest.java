package com.danilomekic.http.model;

import java.util.List;
import java.util.Map;

public record HttpRequest(String requestMethod, String requestTarget, String httpVersion, Map<String, List<String>> requestHeaders, byte[] requestBody) {}
