package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.AccountInfoResponse;
import org.team13.marketplace.dto.item.ItemDto;
import org.team13.marketplace.socket.SocketResponse;

import java.util.List;

public class AccountInfoForms {
    private final MarketplaceClient client;

    public AccountInfoForms(MarketplaceClient client) {
        this.client = client;
    }

    public List<ItemDto> getPurchasedItems() {
        try {
            SocketResponse response = client.send("GET_PURCHASED_ITEMS", null, List.class);

            if ("OK".equalsIgnoreCase(response.getStatus())) {
                return (List<ItemDto>) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error loading purchased items: " + e.getMessage());
        }
        return List.of();
    }

    public List<ItemDto> getSoldItems() {
        try {
            SocketResponse response = client.send("GET_SOLD_ITEMS", null, List.class);

            if ("OK".equalsIgnoreCase(response.getStatus())) {
                return (List<ItemDto>) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error loading sold items: " + e.getMessage());
        }
        return List.of();
    }

    public List<ItemDto> getForSaleItems() {
        try {
            SocketResponse response = client.send("GET_FOR_SALE_ITEMS", null, List.class);

            if ("OK".equalsIgnoreCase(response.getStatus())) {
                return (List<ItemDto>) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error loading for sale items: " + e.getMessage());
        }
        return List.of();
    }

    public AccountInfoResponse getAccountInfo() {
        try {
            SocketResponse response = client.send("GET_ACCOUNT_INFO", null, AccountInfoResponse.class);

            if ("OK".equalsIgnoreCase(response.getStatus())) {
                return (AccountInfoResponse) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error loading account info: " + e.getMessage());
        }
        return null;
    }
}
