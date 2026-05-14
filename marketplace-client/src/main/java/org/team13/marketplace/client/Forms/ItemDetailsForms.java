package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.AccountInfoResponse;
import org.team13.marketplace.dto.transaction.PurchaseRequest;
import org.team13.marketplace.socket.SocketResponse;

public class ItemDetailsForms {
    private final MarketplaceClient client;

    public ItemDetailsForms(MarketplaceClient client) {
        this.client = client;
    }

    public boolean purchaseItem(String itemId) {
        try {
            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setItemId(itemId);
            purchaseRequest.setQuantity(1);

            SocketResponse response = client.send("PURCHASE_ITEM", purchaseRequest, String.class);

            return "OK".equalsIgnoreCase(response.getStatus());
        } catch (Exception e) {
            System.err.println("Error purchasing item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public AccountInfoResponse getUpdatedAccountInfo() {
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
