package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.AccountInfoResponse;
import org.team13.marketplace.dto.item.ItemDto;
import org.team13.marketplace.socket.SocketResponse;
import tools.jackson.core.type.TypeReference;

import java.util.List;

public class AccountInfoForms {
    private final MarketplaceClient client;

    public AccountInfoForms(MarketplaceClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public AccountInfoResponse getAccountInfo() {
        try {
            SocketResponse response = client.send("ACCOUNT", java.util.Map.of(), AccountInfoResponse.class);

            if ("OK".equalsIgnoreCase(response.getStatus())) {
                return (AccountInfoResponse) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error loading account info: " + e.getMessage());
        }
        return null;
    }

    public List<ItemDto> getPurchasedItems() {
        AccountInfoResponse info = getAccountInfo();
        return info != null && info.getPurchasedItems() != null ? info.getPurchasedItems() : List.of();
    }

    public List<ItemDto> getSoldItems() {
        AccountInfoResponse info = getAccountInfo();
        return info != null && info.getSoldItems() != null ? info.getSoldItems() : List.of();
    }

    public List<ItemDto> getForSaleItems() {
        AccountInfoResponse info = getAccountInfo();
        return info != null && info.getOwnedItems() != null ? info.getOwnedItems() : List.of();
    }
}
