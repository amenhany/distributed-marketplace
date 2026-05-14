package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.RegisterRequest; // Ensure this DTO exists
import org.team13.marketplace.socket.SocketResponse;

public class RegisterForms {
    private final MarketplaceClient client;

    public RegisterForms(MarketplaceClient client) {
        this.client = client;
    }

    public void register(String username, String password, String email) throws Exception {
        // Create the DTO
        RegisterRequest regReq = new RegisterRequest();
        regReq.setUsername(username);
        regReq.setPassword(password);
        regReq.setEmail(email);

        // Send request
        SocketResponse response = client.send("REGISTER", regReq, null);

        // Check response
        if (!"OK".equalsIgnoreCase(response.getStatus())) {
            throw new Exception(response.getMessage());
        }
    }
}