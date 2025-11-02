package com.danilomekic.http.server.exception;

/** RFC 9110: Section 15.6.6. */
public class HttpVersionNotSupportedException extends HttpException {
    public HttpVersionNotSupportedException(String version) {
        super(
                505,
                "HTTP Version Not Supported",
                "HTTP version '" + version + "' is not supported by the server");
    }
}
