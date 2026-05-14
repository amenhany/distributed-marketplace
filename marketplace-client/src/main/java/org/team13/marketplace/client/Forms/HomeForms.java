package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.item.ItemDto;
import org.team13.marketplace.socket.SocketResponse;
import tools.jackson.core.type.TypeReference;

import java.util.List;

public class HomeForms {
    private final MarketplaceClient client;

    public HomeForms(MarketplaceClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public List<ItemDto> searchItems(String searchTerm) {
        try {
            java.util.Map<String, String> payload = java.util.Map.of("query", searchTerm == null ? "" : searchTerm);

            SocketResponse response = client.send("SEARCH", payload, new TypeReference<List<ItemDto>>() {});

            if ("OK".equalsIgnoreCase(response.getStatus())) {
                return (List<ItemDto>) response.getData();
            } else {
                System.err.println("Failed to search items: " + response.getMessage());
                return List.of();
            }
        } catch (Exception e) {
            System.err.println("Error searching items: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<ItemDto> getAllItems() {
        return searchItems("");
    }
}
