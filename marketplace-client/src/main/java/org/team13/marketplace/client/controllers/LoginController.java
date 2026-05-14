package org.team13.marketplace.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import org.team13.marketplace.client.Forms.LoginForms;
import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.AccountInfoResponse;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private MarketplaceClient client;
    private PanelSwitcher panelSwitcher;

    public void setClient(MarketplaceClient client) {
        this.client = client;
    }

    public void setPanelSwitcher(PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // 1. Basic UI Validation
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            return;
        }

        statusLabel.setText("Logging in...");

        // 2. Run network task in a background thread to keep UI responsive
        new Thread(() -> {
            try {
                LoginForms loginLogic = new LoginForms(client);
                AccountInfoResponse info = loginLogic.login(username, password);

                // 3. UI updates must happen on the JavaFX Thread
                Platform.runLater(() -> panelSwitcher.switchToHomePanel(info));

            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Login Failed: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleRegister() {
        panelSwitcher.switchToRegisterPanel();
    }
}
