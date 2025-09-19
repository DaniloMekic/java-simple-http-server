package com.danilomekic.http.model;

import java.util.List;
import java.util.Map;

 /*
 * RFC 9112
 * HTTP Request Message Format = Request-line<CR><LF>
 *                               *[Header-line]<CR><LF>
 *                               <CR><LF>
 *                               Body
 *
 * <CR> = \u000D | 13 | 0x0D | 0o15 | 0b00001101
 * <LF> = \u000A | 10 | 0x0A | 0o12 | 0b00001010
*/
public record HttpRequest(String requestMethod, String requestTarget, String httpVersion, Map<String, List<String>> requestHeaders, byte[] requestBody) {}
