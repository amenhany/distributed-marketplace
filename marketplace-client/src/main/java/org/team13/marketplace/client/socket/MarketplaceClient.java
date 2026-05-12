package org.team13.marketplace.client.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MarketplaceClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            System.out.println("Connected to server on " + host + ":" + port);
            
            // Start a thread to listen for messages from the server
            new Thread(this::listenToServer).start();
            
        } catch (Exception e) {
            System.err.println("Could not connect to socket server: " + e.getMessage());
        }
    }

    private void listenToServer() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Server says: " + message);
            }
        } catch (Exception e) {
            System.out.println("Connection lost.");
        }
    }

    public void sendMessage(String msg) {
        if (out != null) out.println(msg);
    }
}