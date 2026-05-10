package org.team13.marketplace.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class MarketplaceSocketServer implements CommandLineRunner {

    @Value("${socket.server.port:9090}")
    private int port;

    private final ClientHandlerFactory factory;
    private final ExecutorService pool = Executors.newFixedThreadPool(50);

    @Override
    public void run(String... args) {
        // Run in a new thread so it doesn't block the main Spring Boot thread
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                log.info("Socket server on port {}", port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    log.info("New client connected: {}", clientSocket.getInetAddress());

                    pool.submit(factory.create(clientSocket));
                }
            } catch (IOException e) {
                log.error("Socket Server Error: ", e);
            }
        }, "socket-server").start();
    }
}