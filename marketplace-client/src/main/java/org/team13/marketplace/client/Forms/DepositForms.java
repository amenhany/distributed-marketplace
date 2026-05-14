package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.socket.SocketResponse;

import java.util.Map;

public class DepositForms {
    private final MarketplaceClient client;

    public DepositForms(MarketplaceClient client) {
        this.client = client;
    }

    public double deposit(double amount) throws Exception {
        Map<String, Object> payload = Map.of("amount", amount);
        SocketResponse response = client.send("DEPOSIT", payload, Double.class);

        if ("OK".equalsIgnoreCase(response.getStatus())) {
            return (Double) response.getData();
        } else {
            throw new Exception(response.getMessage());
        }
    }
}
