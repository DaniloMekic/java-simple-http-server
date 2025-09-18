package com.danilomekic.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSocketAcceptor implements SocketAcceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSocketAcceptor.class);

    private ServerSocket serverSocket;
    private volatile boolean isRunning;
    private ConnectionHandler connectionHandler;

    @Override
    public void start(int port, ConnectionHandler connectionHandler) throws IOException {
        LOGGER.info("Initializing server socket");
        this.connectionHandler = connectionHandler;
        this.serverSocket = new ServerSocket(port);
        LOGGER.info("Server socket initialized on port {}", port);
        this.isRunning = true;

        new Thread(() -> {
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.info("New connection from: {}", clientSocket.getRemoteSocketAddress());
                    connectionHandler.handle(clientSocket);
                } catch (Exception e) {
                    LOGGER.error("Fail to accept connection from a client: {}", e);
                }
            }
        }).start();
    }

    @Override
    public void stop() throws IOException {
        isRunning = false;

        try {
            LOGGER.info("Closing server socket");
            serverSocket.close();
        } catch (Exception e) {
            LOGGER.error("Failed to close server socket: {}", e);
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
