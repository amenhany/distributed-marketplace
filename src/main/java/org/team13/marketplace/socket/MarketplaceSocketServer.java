package org.team13.marketplace.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class MarketplaceSocketServer implements CommandLineRunner {

    @Autowired
    private ClientHandler socketHandler;

    @Override
    public void run(String... args) {
        // Run in a new thread so it doesn't block the main Spring Boot thread
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(9090)) {
                System.out.println("Sytem Socket Server started on port 9090...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    new Thread(() -> socketHandler.handleClient(clientSocket)).start();
                }
            } catch (Exception e) {
                System.err.println("Socket Server Error: " + e.getMessage());
            }
        }).start();
    }
}