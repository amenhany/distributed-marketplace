package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.RegisterRequest;
import org.team13.marketplace.socket.SocketResponse;

import java.util.Scanner;

public class RegisterForms {

    private final MarketplaceClient client;
    private final Scanner scanner = new Scanner(System.in);

    public RegisterForms(MarketplaceClient client) {
        this.client = client;
    }

    public void show() {

        System.out.println("=== Marketplace Register ===");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = (System.console() != null)
                ? new String(System.console().readPassword())
                : scanner.nextLine();

        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("\n[!] Passwords do not match!");
            return;
        }

        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setUsername(username);
        registerReq.setEmail(email);
        registerReq.setPassword(password);

        try {
            SocketResponse response =
                    client.send("REGISTER", registerReq, String.class);

            if ("OK".equalsIgnoreCase(response.getStatus())) {
                System.out.println("\n[+] Registration Successful!");
                System.out.println("Message: " + response.getMessage());
            } else {
                System.out.println("\n[!] Registration Failed: " + response.getMessage());
            }

        } catch (Exception e) {
            System.out.println("\n[!] Error: " + e.getMessage());
        }
    }
}