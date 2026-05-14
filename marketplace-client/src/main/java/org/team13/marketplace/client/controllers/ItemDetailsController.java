package org.team13.marketplace.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.team13.marketplace.client.Forms.ItemDetailsForms;
import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.AccountInfoResponse;
import org.team13.marketplace.dto.item.ItemDto;

public class ItemDetailsController {
    
    @FXML
    private Label itemNameLabel;
    
    @FXML
    private Label brandLabel;
    
    @FXML
    private Label priceLabel;
    
    @FXML
    private Label sellerLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Button purchaseButton;
    
    private PanelSwitcher panelSwitcher;
    private ItemDetailsForms itemDetailsForms;
    private ItemDto currentItem;
    private AccountInfoResponse currentUser;
    
    public void setClient(MarketplaceClient client) {
        this.itemDetailsForms = new ItemDetailsForms(client);
    }
    
    public void setPanelSwitcher(PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;
    }
    
    public void setCurrentItem(ItemDto item) {
        this.currentItem = item;
        displayItemDetails();
    }
    
    public void setCurrentUser(AccountInfoResponse currentUser) {
        this.currentUser = currentUser;
    }
    
    @FXML
    private void handleBackToHome() {
        panelSwitcher.switchToHomePanel(currentUser);
    }
    
    @FXML
    private void handlePurchase() {
        if (currentItem == null || currentUser == null) {
            return;
        }
        
        boolean success = itemDetailsForms.purchaseItem(currentItem.getId());
        
        if (success) {
            // Refresh user info after purchase
            AccountInfoResponse updatedInfo = itemDetailsForms.getUpdatedAccountInfo();
            if (updatedInfo != null) {
                panelSwitcher.switchToHomePanel(updatedInfo);
            }
        }
    }
    
    private void displayItemDetails() {
        if (currentItem == null) {
            return;
        }
        
        itemNameLabel.setText(currentItem.getName());
        brandLabel.setText(currentItem.getBrand());
        priceLabel.setText("$" + String.format("%.2f", currentItem.getPrice()));
        sellerLabel.setText(currentItem.getOwnerId());
        statusLabel.setText(currentItem.getStatus() != null ? currentItem.getStatus() : "Available");
        descriptionArea.setText(currentItem.getDescription() != null ? currentItem.getDescription() : "No description available");
        
        // Disable purchase button if item is not available or user is the seller
        if (currentUser != null && 
            (currentItem.getStatus() != null && !currentItem.getStatus().equals("AVAILABLE") ||
             currentItem.getOwnerId().equals(currentUser.getUsername()))) {
            purchaseButton.setDisable(true);
            purchaseButton.setText("Not Available");
        }
    }
}
