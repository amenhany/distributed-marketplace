package org.team13.marketplace.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.team13.marketplace.client.Forms.HomeForms;
import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.item.ItemDto;

import java.util.List;

public class HomeController {
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<ItemDto> itemsTable;
    
    @FXML
    private TableColumn<ItemDto, String> nameColumn;
    
    @FXML
    private TableColumn<ItemDto, String> brandColumn;
    
    @FXML
    private TableColumn<ItemDto, Double> priceColumn;
    
    @FXML
    private TableColumn<ItemDto, String> sellerColumn;
    
    @FXML
    private TableColumn<ItemDto, String> actionColumn;
    
    private PanelSwitcher panelSwitcher;
    private HomeForms homeForms;
    private org.team13.marketplace.dto.auth.AccountInfoResponse currentUser;
    
    public void setClient(MarketplaceClient client) {
        this.homeForms = new HomeForms(client);
    }
    
    public void setPanelSwitcher(PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;
    }
    
    public void setCurrentUser(org.team13.marketplace.dto.auth.AccountInfoResponse currentUser) {
        this.currentUser = currentUser;
    }
    
    @FXML
    public void initialize() {
        // Initialize table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        sellerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        
        // Add view button to action column
        actionColumn.setCellFactory(col -> new TableCell<ItemDto, String>() {
            private final Button viewButton = new Button("View Details");
            
            {
                viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                viewButton.setOnAction(event -> {
                    ItemDto item = getTableView().getItems().get(getIndex());
                    panelSwitcher.switchToItemDetailsPanel(item, currentUser);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
        
        // Load all items on initialization
        loadItems("");
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();
        loadItems(searchTerm);
    }
    
    @FXML
    private void handleAccountInfo() {
        panelSwitcher.switchToAccountInfoPanel(currentUser);
    }
    
    @FXML
    private void handleLogout() {
        panelSwitcher.switchToLoginPanel();
    }
    
    private void loadItems(String searchTerm) {
        List<ItemDto> items = homeForms.searchItems(searchTerm);
        ObservableList<ItemDto> observableItems = FXCollections.observableArrayList(items);
        itemsTable.setItems(observableItems);
    }
}
