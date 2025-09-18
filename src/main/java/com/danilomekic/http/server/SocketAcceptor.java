package com.danilomekic.http.server;

import java.io.IOException;

public interface SocketAcceptor {
    void start(int port, ConnectionHandler handler) throws IOException;
    void stop() throws IOException;
    boolean isRunning();
}
