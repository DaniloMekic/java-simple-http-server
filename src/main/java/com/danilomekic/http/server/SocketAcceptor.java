package com.danilomekic.http.server;

import java.io.IOException;

import com.danilomekic.http.server.handler.ConnectionHandler;

public interface SocketAcceptor {
    void start(int port, ConnectionHandler connectionHandler) throws IOException;
    void stop() throws IOException;
    boolean isRunning();
}
