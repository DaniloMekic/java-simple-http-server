package com.danilomekic.http.server;

import java.net.Socket;

@FunctionalInterface
public interface ConnectionHandler {
    void handle(Socket socket);
}
