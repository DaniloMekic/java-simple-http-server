package com.danilomekic.http.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.danilomekic.http.server.parser.SimpleRequestParser;
import com.danilomekic.http.server.router.HelloRoute;
import com.danilomekic.http.server.router.Router;
import com.danilomekic.http.server.router.SimpleRouter;
import com.danilomekic.http.server.writer.SimpleHttpResponseWriter;

@SpringBootApplication
public class SimpleHttpServerInJavaApplication {
    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleHttpServerInJavaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SimpleHttpServerInJavaApplication.class, args);

        Router httpRequestRouter = new SimpleRouter();
        httpRequestRouter.addRoute(new HelloRoute());

        ConnectionHandler connectionHandler = new SimpleConnectionHandler(
            new SimpleRequestParser(),
            httpRequestRouter,
            new SimpleHttpResponseWriter()
        );
        SocketAcceptor socketAcceptor = new SimpleSocketAcceptor(connectionHandler);

        try {
            LOGGER.info("Initializing socket acceptor");
            socketAcceptor.start(8090);
        } catch (IOException e) {
            LOGGER.error("Failed to initialize socket acceptor");
        }

        /*
         * Shutdown hook for clean exit
         * that prevents OS from putting port in a TIME_WAIT state
         * when socket is forcefully closed
        */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                socketAcceptor.stop();
            } catch (Exception e) {
                LOGGER.error("Server shutdown error: {}", e);
            }
        }));
    }
}
