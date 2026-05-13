package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.LoginRequest;
import org.team13.marketplace.dto.auth.AccountInfoResponse;
import org.team13.marketplace.socket.SocketResponse;

import java.util.Scanner;

public class LoginForms {
    private final MarketplaceClient client;
    private final Scanner scanner = new Scanner(System.in);

    public LoginForms(MarketplaceClient client) {
        this.client = client;
    }

    public void show() {
        System.out.println("=== Marketplace Login ===");
        
        System.out.print("Username/Email: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        // Hides password in real terminals; falls back to visible text in IDEs
        String password = (System.console() != null) 
            ? new String(System.console().readPassword()) 
            : scanner.nextLine();

        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername(username);
        loginReq.setPassword(password);

        try {
            // Send request and map the "data" field to AccountInfoResponse
            SocketResponse response = client.send("LOGIN", loginReq, AccountInfoResponse.class);

            if ("OK".equalsIgnoreCase(response.getStatus())) {
                AccountInfoResponse info = (AccountInfoResponse) response.getData();
                displaySuccess(info);
            } else {
                System.out.println("\n[!] Login Failed: " + response.getMessage());
            }
        } catch (Exception e) {
            System.out.println("\n[!] Error: " + e.getMessage());
        }
    }

    private void displaySuccess(AccountInfoResponse info) {
        System.out.println("\n---------------------------------");
        System.out.println("SUCCESS: Welcome, " + info.getUsername() + "!");
        System.out.println("Wallet Balance: $" + info.getBalance());
        System.out.println("Items Owned: " + info.getOwnedItems().size());
        System.out.println("---------------------------------");
    }
}