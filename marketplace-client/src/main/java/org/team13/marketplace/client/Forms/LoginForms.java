package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.LoginRequest;
import org.team13.marketplace.dto.auth.AccountInfoResponse;
import org.team13.marketplace.socket.SocketResponse;

public class LoginForms {
    private final MarketplaceClient client;

    public LoginForms(MarketplaceClient client) {
        this.client = client;
    }

    /**
     * Executes the login logic.
     * @return AccountInfoResponse on success
     * @throws Exception with the server's error message on failure
     */
    public AccountInfoResponse login(String username, String password) throws Exception {
        // Prepare the DTO
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername(username);
        loginReq.setPassword(password);

        // Communicate with server
        SocketResponse response = client.send("LOGIN", loginReq, AccountInfoResponse.class);

        // Handle logical result
        if ("OK".equalsIgnoreCase(response.getStatus())) {
            return (AccountInfoResponse) response.getData();
        } else {
            // This message will be caught by the Controller's try-catch
            throw new Exception(response.getMessage());
        }
    }
}