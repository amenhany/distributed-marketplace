package org.team13.marketplace.client.Forms;

import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.item.ItemDto;
import org.team13.marketplace.socket.SocketResponse;

import java.util.List;

public class HomeForms {
    private final MarketplaceClient client;

    public HomeForms(MarketplaceClient client) {
        this.client = client;
    }

    public List<ItemDto> searchItems(String searchTerm) {
        try {
            ItemDto searchRequest = new ItemDto();
            searchRequest.setName(searchTerm);
            searchRequest.setBrand(searchTerm);

            SocketResponse response = client.send("SEARCH_ITEMS", searchRequest, List.class);

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
