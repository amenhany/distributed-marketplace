package org.team13.marketplace.client.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.AccountInfoResponse;
import org.team13.marketplace.dto.item.ItemDto;

import java.io.IOException;

public class PanelSwitcher {
    
    private Stage stage;
    private MarketplaceClient client;
    
    public PanelSwitcher(Stage stage, MarketplaceClient client) {
        this.stage = stage;
        this.client = client;
    }
    
    public void switchToLoginPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_panel.fxml"));
            Parent root = loader.load();
            
            LoginController controller = loader.getController();
            controller.setClient(client);
            controller.setPanelSwitcher(this);
            
            Scene scene = new Scene(root, 400, 300);
            stage.setScene(scene);
            stage.setTitle("Marketplace - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void switchToRegisterPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register_panel.fxml"));
            Parent root = loader.load();
            
            RegisterController controller = loader.getController();
            controller.setClient(client);
            controller.setPanelSwitcher(this);
            
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.setTitle("Marketplace - Register");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void switchToHomePanel(AccountInfoResponse currentUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home_panel.fxml"));
            Parent root = loader.load();
            
            HomeController controller = loader.getController();
            controller.setClient(client);
            controller.setPanelSwitcher(this);
            controller.setCurrentUser(currentUser);
            
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Marketplace - Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void switchToAccountInfoPanel(AccountInfoResponse currentUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/account_info_panel.fxml"));
            Parent root = loader.load();
            
            AccountInfoController controller = loader.getController();
            controller.setClient(client);
            controller.setPanelSwitcher(this);
            controller.setCurrentUser(currentUser);
            
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Marketplace - Account Info");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToDepositPanel(AccountInfoResponse currentUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/deposit_panel.fxml"));
            Parent root = loader.load();

            DepositController controller = loader.getController();
            controller.setClient(client);
            controller.setPanelSwitcher(this);
            controller.setCurrentUser(currentUser);

            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.setTitle("Marketplace - Deposit");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void switchToItemDetailsPanel(ItemDto item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/item_details_panel.fxml"));
            Parent root = loader.load();
            
            ItemDetailsController controller = loader.getController();
            controller.setClient(client);
            controller.setPanelSwitcher(this);
            controller.setCurrentItem(item);
            
            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.setTitle("Marketplace - Item Details");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void switchToItemDetailsPanel(ItemDto item, AccountInfoResponse currentUser) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/item_details_panel.fxml"));
            Parent root = loader.load();
            
            ItemDetailsController controller = loader.getController();
            controller.setClient(client);
            controller.setPanelSwitcher(this);
            controller.setCurrentItem(item);
            controller.setCurrentUser(currentUser);
            
            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.setTitle("Marketplace - Item Details");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
