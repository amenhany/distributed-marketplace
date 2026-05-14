package org.team13.marketplace.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.team13.marketplace.client.Forms.AccountInfoForms;
import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.AccountInfoResponse;
import org.team13.marketplace.dto.item.ItemDto;

import java.util.List;

public class AccountInfoController {
    
    @FXML
    private Label balanceLabel;
    
    @FXML
    private TableView<ItemDto> purchasedItemsTable;
    
    @FXML
    private TableColumn<ItemDto, String> purchasedNameColumn;
    
    @FXML
    private TableColumn<ItemDto, String> purchasedBrandColumn;
    
    @FXML
    private TableColumn<ItemDto, Double> purchasedPriceColumn;
    
    @FXML
    private TableColumn<ItemDto, String> purchasedDateColumn;
    
    @FXML
    private TableView<ItemDto> soldItemsTable;
    
    @FXML
    private TableColumn<ItemDto, String> soldNameColumn;
    
    @FXML
    private TableColumn<ItemDto, String> soldBrandColumn;
    
    @FXML
    private TableColumn<ItemDto, Double> soldPriceColumn;
    
    @FXML
    private TableColumn<ItemDto, String> soldDateColumn;
    
    @FXML
    private TableView<ItemDto> forSaleItemsTable;
    
    @FXML
    private TableColumn<ItemDto, String> forSaleNameColumn;
    
    @FXML
    private TableColumn<ItemDto, String> forSaleBrandColumn;
    
    @FXML
    private TableColumn<ItemDto, Double> forSalePriceColumn;
    
    @FXML
    private TableColumn<ItemDto, String> forSaleStatusColumn;
    
    private PanelSwitcher panelSwitcher;
    private AccountInfoForms accountInfoForms;
    private AccountInfoResponse currentUser;
    
    public void setClient(MarketplaceClient client) {
        this.accountInfoForms = new AccountInfoForms(client);
    }
    
    public void setPanelSwitcher(PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;
    }
    
    public void setCurrentUser(AccountInfoResponse currentUser) {
        this.currentUser = currentUser;
        loadAccountInfo();
    }
    
    @FXML
    public void initialize() {
        // Initialize purchased items table
        purchasedNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        purchasedBrandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        purchasedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        purchasedDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        // Initialize sold items table
        soldNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        soldBrandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        soldPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        soldDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        // Initialize for sale items table
        forSaleNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        forSaleBrandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        forSalePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        forSaleStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    @FXML
    private void handleBackToHome() {
        panelSwitcher.switchToHomePanel(currentUser);
    }
    
    @FXML
    private void handleLogout() {
        panelSwitcher.switchToLoginPanel();
    }

    @FXML
    private void handleDeposit() {
        panelSwitcher.switchToDepositPanel(currentUser);
    }
    
    private void loadAccountInfo() {
        if (currentUser == null) {
            return;
        }
        
        // Update balance
        balanceLabel.setText("$" + String.format("%.2f", currentUser.getBalance()));
        
        // Load purchased items
        loadPurchasedItems();
        
        // Load sold items
        loadSoldItems();
        
        // Load items for sale
        loadForSaleItems();
    }
    
    private void loadPurchasedItems() {
        List<ItemDto> items = accountInfoForms.getPurchasedItems();
        ObservableList<ItemDto> observableItems = FXCollections.observableArrayList(items);
        purchasedItemsTable.setItems(observableItems);
    }
    
    private void loadSoldItems() {
        List<ItemDto> items = accountInfoForms.getSoldItems();
        ObservableList<ItemDto> observableItems = FXCollections.observableArrayList(items);
        soldItemsTable.setItems(observableItems);
    }
    
    private void loadForSaleItems() {
        List<ItemDto> items = accountInfoForms.getForSaleItems();
        ObservableList<ItemDto> observableItems = FXCollections.observableArrayList(items);
        forSaleItemsTable.setItems(observableItems);
    }
}
